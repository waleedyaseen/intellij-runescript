package io.runescript.plugin.ide.codeInsight.smartEnter

import com.intellij.application.options.CodeStyle
import com.intellij.codeInsight.editorActions.smartEnter.SmartEnterProcessor
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.lang.psi.RsFile
import io.runescript.plugin.lang.psi.RsStringLiteralExpression

class RsSmartEnterProcessor : SmartEnterProcessor() {
    override fun process(
        project: Project,
        editor: Editor,
        psiFile: PsiFile,
    ): Boolean {
        if (psiFile !is RsFile) return false
        val document = editor.document
        val caretOffset = editor.caretModel.offset
        val line = document.getLineNumber(caretOffset)
        val lineStart = document.getLineStartOffset(line)
        val lineEnd = document.getLineEndOffset(line)
        if (document.charsSequence.subSequence(caretOffset, lineEnd).isNotBlank()) return false
        if (isInsideLiteralOrComment(psiFile, caretOffset)) return false

        val rawLineText = document.charsSequence.subSequence(lineStart, caretOffset).toString()
        val completion = completionFor(rawLineText.trimEnd()) ?: return false
        val lineIndent = rawLineText.takeWhile { it == ' ' || it == '\t' }
        val indentOptions = CodeStyle.getIndentOptions(psiFile)
        val indentUnit = if (indentOptions.USE_TAB_CHARACTER) "\t" else " ".repeat(indentOptions.INDENT_SIZE)
        val bodyIndent = lineIndent + indentUnit
        when (completion.mode) {
            EnterMode.NORMAL -> {
                document.insertString(caretOffset, completion.suffix)
                editor.caretModel.moveToOffset(caretOffset + completion.suffix.length)
                PsiDocumentManager.getInstance(project).commitDocument(document)
                EditorActionManager
                    .getInstance()
                    .getActionHandler(IdeActions.ACTION_EDITOR_ENTER)
                    .execute(editor, editor.caretModel.currentCaret, DataContext.EMPTY_CONTEXT)
            }

            EnterMode.INDENT -> {
                val insertion = completion.suffix + "\n" + bodyIndent
                document.insertString(caretOffset, insertion)
                editor.caretModel.moveToOffset(caretOffset + insertion.length)
                PsiDocumentManager.getInstance(project).commitDocument(document)
            }

            EnterMode.BRACES -> {
                val insertion = completion.suffix + "\n" + bodyIndent + "\n" + lineIndent + "}"
                document.insertString(caretOffset, insertion)
                editor.caretModel.moveToOffset(caretOffset + completion.suffix.length + 1 + bodyIndent.length)
                PsiDocumentManager.getInstance(project).commitDocument(document)
            }
        }
        return true
    }

    private fun isInsideLiteralOrComment(
        file: PsiFile,
        offset: Int,
    ): Boolean {
        if (offset == 0) return false
        val leaf = file.findElementAt(offset - 1) ?: return false
        return leaf is PsiComment || PsiTreeUtil.getParentOfType(leaf, RsStringLiteralExpression::class.java, false) != null
    }

    private fun completionFor(lineText: String): Completion? {
        val trimmed = lineText.trimStart()
        controlFlowCompletion(trimmed)?.let { return it }
        if (SWITCH_CASE.matches(trimmed) && !trimmed.endsWith(':')) {
            return Completion(" :", EnterMode.INDENT)
        }
        if (!needsSemicolon(trimmed)) return null
        val missingParentheses = parenBalance(trimmed).coerceAtLeast(0)
        return Completion(")".repeat(missingParentheses) + ";")
    }

    private fun controlFlowCompletion(text: String): Completion? {
        val header = CONTROL_FLOW_HEADER.find(text) ?: return null
        val openingParenthesis = text.indexOf('(', header.range.first)
        val closingParenthesis = matchingParenthesis(text, openingParenthesis)
        if (closingParenthesis == null) {
            val missingParentheses = parenBalance(text)
            if (missingParentheses <= 0) return null
            return Completion(")".repeat(missingParentheses) + " {", EnterMode.BRACES)
        }

        val inlineStatement = text.substring(closingParenthesis + 1).trim()
        if (inlineStatement.isEmpty()) return Completion(" {", EnterMode.BRACES)
        if (!needsSemicolon(inlineStatement)) return null
        val missingParentheses = parenBalance(inlineStatement).coerceAtLeast(0)
        return Completion(")".repeat(missingParentheses) + ";")
    }

    private fun matchingParenthesis(
        text: String,
        openingOffset: Int,
    ): Int? {
        var depth = 0
        var quoted = false
        var escaped = false
        for (offset in openingOffset until text.length) {
            val character = text[offset]
            when {
                escaped -> {
                    escaped = false
                }

                character == '\\' && quoted -> {
                    escaped = true
                }

                character == '"' -> {
                    quoted = !quoted
                }

                !quoted && character == '(' -> {
                    depth++
                }

                !quoted && character == ')' -> {
                    depth--
                    if (depth == 0) return offset
                }
            }
        }
        return null
    }

    private fun needsSemicolon(text: String): Boolean {
        if (text.isEmpty() || text.last() in charArrayOf(';', '{', '}', ':', ',')) return false
        return DECLARATION.containsMatchIn(text) ||
            RETURN.containsMatchIn(text) ||
            ASSIGNMENT.containsMatchIn(text) ||
            CALL.containsMatchIn(text) ||
            FIX_EXPRESSION.containsMatchIn(text)
    }

    private fun parenBalance(text: String): Int {
        var balance = 0
        var quoted = false
        var escaped = false
        for (character in text) {
            when {
                escaped -> escaped = false
                character == '\\' && quoted -> escaped = true
                character == '"' -> quoted = !quoted
                !quoted && character == '(' -> balance++
                !quoted && character == ')' -> balance--
            }
        }
        return balance
    }

    private data class Completion(
        val suffix: String,
        val mode: EnterMode = EnterMode.NORMAL,
    )

    private enum class EnterMode {
        NORMAL,
        INDENT,
        BRACES,
    }

    companion object {
        private val CONTROL_FLOW_HEADER = Regex("""^(?:if|while|switch_[A-Za-z0-9_.:]+)\s*\(""")
        private val SWITCH_CASE = Regex("""^case\s+\S.*""")
        private val DECLARATION = Regex("""^def_[A-Za-z0-9_.:]+\s+\$\S+.*""")
        private val RETURN = Regex("""^return(?:\s|\(|$).*""")
        private val ASSIGNMENT = Regex("""^(?:\$|%)[A-Za-z0-9_.:]+(?:\([^)]*\))?\s*=.*""")
        private val CALL = Regex("""^[.~]?[A-Za-z0-9_.:]+\s*\(.*""")
        private val FIX_EXPRESSION = Regex("""^(?:\+\+|--)?\$[A-Za-z0-9_.:]+(?:\+\+|--)$""")
    }
}

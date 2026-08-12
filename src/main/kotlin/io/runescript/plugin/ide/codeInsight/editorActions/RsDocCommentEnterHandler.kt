package io.runescript.plugin.ide.codeInsight.editorActions

import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegate
import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegateAdapter
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiFile
import io.runescript.plugin.lang.RuneScript

class RsDocCommentEnterHandler : EnterHandlerDelegateAdapter() {
    override fun preprocessEnter(
        file: PsiFile,
        editor: Editor,
        caretOffsetRef: Ref<Int>,
        caretAdvance: Ref<Int>,
        dataContext: DataContext,
        originalHandler: EditorActionHandler?,
    ): EnterHandlerDelegate.Result {
        if (file.language != RuneScript) return EnterHandlerDelegate.Result.Continue
        val document = editor.document
        val offset = editor.caretModel.offset
        if (offset < 3 || document.charsSequence.subSequence(offset - 3, offset).toString() != "/**") {
            return EnterHandlerDelegate.Result.Continue
        }
        val line = document.getLineNumber(offset)
        val lineStart = document.getLineStartOffset(line)
        val beforeComment = document.charsSequence.subSequence(lineStart, offset - 3).toString()
        if (beforeComment.isNotBlank()) return EnterHandlerDelegate.Result.Continue

        val signature =
            SCRIPT_SIGNATURE.find(document.charsSequence.subSequence(offset, document.textLength))
                ?: return EnterHandlerDelegate.Result.Continue
        val indent = beforeComment
        val tags = mutableListOf<String>()
        signature.groupValues[1]
            .split(',')
            .map(String::trim)
            .filter(String::isNotEmpty)
            .mapNotNull { parameter -> PARAMETER_NAME.find(parameter)?.value }
            .mapTo(tags) { "@param $it" }
        val returns =
            signature.groupValues[2]
                .split(',')
                .map(String::trim)
                .filter(String::isNotEmpty)
        repeat(returns.size) { tags += "@return" }

        val stub =
            buildString {
                append("\n$indent * ")
                if (tags.isNotEmpty()) {
                    append("\n$indent *")
                    tags.forEach { append("\n$indent * $it") }
                }
                append("\n$indent */")
            }
        document.insertString(offset, stub)
        editor.caretModel.moveToOffset(offset + 4 + indent.length)
        caretOffsetRef.set(editor.caretModel.offset)
        return EnterHandlerDelegate.Result.Stop
    }

    private companion object {
        val SCRIPT_SIGNATURE =
            """^\s*\[[^\]\r\n]+](?:\(([^)]*)\))?(?:\(([^)]*)\))?""".toRegex()
        val PARAMETER_NAME = """\$[A-Za-z0-9_.:]+""".toRegex()
    }
}

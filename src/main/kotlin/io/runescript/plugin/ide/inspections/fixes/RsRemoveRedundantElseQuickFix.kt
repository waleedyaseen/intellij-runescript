package io.runescript.plugin.ide.inspections.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.parentOfType
import io.runescript.plugin.lang.psi.RsBlockStatement
import io.runescript.plugin.lang.psi.RsIfStatement

class RsRemoveRedundantElseQuickFix : LocalQuickFix {
    override fun getFamilyName(): String = "Remove redundant 'else'"

    override fun applyFix(
        project: Project,
        descriptor: ProblemDescriptor,
    ) {
        val ifStatement = descriptor.psiElement.parentOfType<RsIfStatement>() ?: return
        val elseKeyword = ifStatement.getElse() ?: return
        val elseBranch = ifStatement.falseStatement ?: return
        val documentManager = PsiDocumentManager.getInstance(project)
        val document = documentManager.getDocument(ifStatement.containingFile) ?: return
        val lineStart = document.getLineStartOffset(document.getLineNumber(ifStatement.textOffset))
        val targetIndent =
            document.charsSequence
                .subSequence(lineStart, ifStatement.textOffset)
                .takeWhile { it.isWhitespace() }
                .toString()
        val branchText =
            when (elseBranch) {
                is RsBlockStatement -> elseBranch.statementList.text
                else -> elseBranch.text
            }
        val replacement = liftBranch(branchText, targetIndent)
        var replacementStart = elseKeyword.textRange.startOffset
        var previous = elseKeyword.prevSibling
        while (previous is PsiWhiteSpace && previous.textRange.endOffset <= elseKeyword.textRange.startOffset) {
            replacementStart = previous.textRange.startOffset
            previous = previous.prevSibling
        }
        document.replaceString(replacementStart, elseBranch.textRange.endOffset, replacement)
        documentManager.commitDocument(document)
    }

    private fun liftBranch(
        text: String,
        targetIndent: String,
    ): String {
        val lines =
            text
                .lines()
                .dropWhile(String::isBlank)
                .dropLastWhile(String::isBlank)
        if (lines.isEmpty()) return ""
        val sourceIndent = lines.filterNot(String::isBlank).minOf { line -> line.indexOfFirst { !it.isWhitespace() } }
        return lines.joinToString(prefix = "\n", separator = "\n") { line ->
            if (line.isBlank()) "" else targetIndent + line.drop(sourceIndent)
        }
    }
}

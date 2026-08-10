package io.runescript.plugin.ide.inspections.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.lang.doc.psi.impl.RsDocTag

class RsRemoveInvalidParameterMetadataQuickFix : LocalQuickFix {
    override fun getFamilyName(): String = "Remove invalid parameter metadata"

    override fun equals(other: Any?): Boolean = other is RsRemoveInvalidParameterMetadataQuickFix

    override fun hashCode(): Int = javaClass.hashCode()

    override fun applyFix(
        project: Project,
        descriptor: ProblemDescriptor,
    ) {
        val element = descriptor.psiElement
        val tag = (element as? RsDocTag) ?: PsiTreeUtil.getParentOfType(element, RsDocTag::class.java, false) ?: return
        val document = PsiDocumentManager.getInstance(project).getDocument(tag.containingFile) ?: return
        val startLine = document.getLineNumber(tag.textRange.startOffset)
        val startOffset = document.getLineStartOffset(startLine)
        val lineEndOffset = document.getLineEndOffset(startLine)
        val lineText = document.getText(TextRange(startOffset, lineEndOffset))

        if ("/**" in lineText || "*/" in lineText) {
            document.deleteString(tag.textRange.startOffset, tag.textRange.endOffset)
        } else {
            var endLine = startLine + 1
            while (endLine < document.lineCount) {
                val nextLineStart = document.getLineStartOffset(endLine)
                val nextLineEnd = document.getLineEndOffset(endLine)
                val nextLine = document.getText(TextRange(nextLineStart, nextLineEnd))
                if (TAG_OR_COMMENT_END.matches(nextLine)) break
                endLine++
            }
            val endOffset = if (endLine < document.lineCount) document.getLineStartOffset(endLine) else document.textLength
            document.deleteString(startOffset, endOffset)
        }
        PsiDocumentManager.getInstance(project).commitDocument(document)
    }

    private companion object {
        val TAG_OR_COMMENT_END = Regex("""\s*\*\s*(?:@\S+|/).*""")
    }
}

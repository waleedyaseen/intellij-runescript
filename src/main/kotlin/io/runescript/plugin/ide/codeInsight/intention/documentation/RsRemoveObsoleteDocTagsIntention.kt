package io.runescript.plugin.ide.codeInsight.intention.documentation

import com.intellij.codeInsight.intention.BaseElementAtCaretIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.ide.doc.findDoc
import io.runescript.plugin.lang.psi.RsScript

class RsRemoveObsoleteDocTagsIntention : BaseElementAtCaretIntentionAction() {
    override fun getFamilyName(): String = RsBundle.message("intention.remove.obsolete.rsdoc.tags.family.name")

    override fun getText(): String = RsBundle.message("intention.remove.obsolete.rsdoc.tags.name")

    override fun invoke(
        project: Project,
        editor: Editor,
        element: PsiElement,
    ) {
        val script = element.findScriptAtSignature() ?: return
        val doc = script.findDoc() ?: return
        val updated = removeObsoleteTags(script, doc.text)
        if (updated == doc.text) return

        editor.document.replaceString(doc.textRange.startOffset, doc.textRange.endOffset, updated)
        PsiDocumentManager.getInstance(project).commitDocument(editor.document)
    }

    override fun isAvailable(
        project: Project,
        editor: Editor,
        element: PsiElement,
    ): Boolean {
        val script = element.findScriptAtSignature() ?: return false
        val doc = script.findDoc() ?: return false
        return removeObsoleteTags(script, doc.text) != doc.text
    }

    private fun removeObsoleteTags(
        script: RsScript,
        comment: String,
    ): String {
        val parameters =
            script.parameterList
                ?.parameterList
                ?.mapNotNullTo(mutableSetOf()) { it.localVariableExpression?.nameLiteral?.text }
                .orEmpty()
        val returnCount = script.returnList?.typeNameList?.size ?: 0
        var seenReturns = 0
        val lines = comment.lines()
        return buildList {
            var index = 0
            while (index < lines.size) {
                val line = lines[index]
                val parameter = PARAMETER_TAG.matchEntire(line)?.groupValues?.get(1)
                val keep =
                    when {
                        parameter != null -> parameter in parameters
                        RETURN_TAG.matches(line) -> seenReturns++ < returnCount
                        else -> true
                    }
                if (keep) {
                    add(line)
                    index++
                } else {
                    index++
                    while (index < lines.size && !TAG_START.matches(lines[index]) && !CLOSING.matches(lines[index])) {
                        index++
                    }
                }
            }
        }.joinToString("\n")
    }

    companion object {
        private val PARAMETER_TAG = Regex("""\s*\*\s*@param(?:meta)?\s+(\S+)(?:\s.*)?""")
        private val RETURN_TAG = Regex("""\s*\*\s*@return(?:\s.*)?""")
        private val TAG_START = Regex("""\s*\*\s*@\S+(?:\s.*)?""")
        private val CLOSING = Regex("""\s*\*/\s*""")
    }
}

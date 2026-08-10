package io.runescript.plugin.ide.codeInsight.intention.documentation

import com.intellij.codeInsight.intention.BaseElementAtCaretIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.ide.doc.findDoc
import io.runescript.plugin.lang.psi.RsScript

class RsReorderDocTagsIntention : BaseElementAtCaretIntentionAction() {
    override fun getFamilyName(): String = RsBundle.message("intention.reorder.rsdoc.tags.family.name")

    override fun getText(): String = RsBundle.message("intention.reorder.rsdoc.tags.name")

    override fun invoke(
        project: Project,
        editor: Editor,
        element: PsiElement,
    ) {
        val script = element.findScriptAtSignature() ?: return
        val doc = script.findDoc() ?: return
        val reordered = reorderTags(script, doc.text)
        if (reordered == doc.text) return
        editor.document.replaceString(doc.textRange.startOffset, doc.textRange.endOffset, reordered)
        PsiDocumentManager.getInstance(project).commitDocument(editor.document)
    }

    override fun isAvailable(
        project: Project,
        editor: Editor,
        element: PsiElement,
    ): Boolean {
        val script = element.findScriptAtSignature() ?: return false
        val doc = script.findDoc() ?: return false
        return reorderTags(script, doc.text) != doc.text
    }

    private fun reorderTags(
        script: RsScript,
        comment: String,
    ): String {
        val lines = comment.lines()
        val closingIndex = lines.indexOfLast { CLOSING.matches(it) }
        val tagStarts = lines.indices.filter { it < closingIndex && TAG_START.matches(lines[it]) }
        if (tagStarts.size < 2) return comment

        val blocks =
            tagStarts.mapIndexed { index, start ->
                val end = tagStarts.getOrNull(index + 1) ?: closingIndex
                TagBlock(lines.subList(start, end))
            }
        val parameterNames =
            script.parameterList
                ?.parameterList
                ?.mapNotNull { it.localVariableExpression?.nameLiteral?.text }
                .orEmpty()
        val parameterBlocks = blocks.filter { it.parameterName in parameterNames }
        val orderedParameters =
            parameterNames.flatMap { name -> parameterBlocks.filter { it.parameterName == name } }
        val returns = blocks.filter { it.tagName == "return" }
        val knownBlocks = (parameterBlocks + returns).toSet()
        val remaining = blocks.filterNot { it in knownBlocks }
        val ordered = orderedParameters + returns + remaining

        return buildList {
            addAll(lines.subList(0, tagStarts.first()))
            ordered.forEach { addAll(it.lines) }
            addAll(lines.subList(closingIndex, lines.size))
        }.joinToString("\n")
    }

    private data class TagBlock(
        val lines: List<String>,
    ) {
        private val match = TAG_START.matchEntire(lines.first())
        val tagName: String? = match?.groupValues?.get(1)
        val parameterName: String? =
            if (tagName == "param" || tagName == "parammeta") match?.groupValues?.get(2)?.takeIf { it.isNotEmpty() } else null
    }

    companion object {
        private val TAG_START = Regex("""\s*\*\s*@(\S+)(?:\s+(\S+))?.*""")
        private val CLOSING = Regex("""\s*\*/\s*""")
    }
}

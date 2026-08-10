package io.runescript.plugin.ide.codeInsight.intention.documentation

import com.intellij.codeInsight.intention.BaseElementAtCaretIntentionAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.ide.codeInsight.intention.findScriptAtSignature
import io.runescript.plugin.ide.doc.findDoc
import io.runescript.plugin.lang.doc.psi.api.RsDoc
import io.runescript.plugin.lang.psi.RsScript

class RsAddMissingDocTagsIntention : BaseElementAtCaretIntentionAction() {
    override fun getFamilyName(): String = RsBundle.message("intention.add.missing.rsdoc.tags.family.name")

    override fun getText(): String = RsBundle.message("intention.add.missing.rsdoc.tags.name")

    override fun invoke(
        project: Project,
        editor: Editor,
        element: PsiElement,
    ) {
        val script = element.findScriptAtSignature() ?: return
        applyTo(project, editor.document, script)
    }

    override fun isAvailable(
        project: Project,
        editor: Editor,
        element: PsiElement,
    ): Boolean {
        val script = element.findScriptAtSignature() ?: return false
        return isNeeded(script)
    }

    internal fun isNeeded(script: RsScript): Boolean {
        val doc = script.findDoc() ?: return false
        return !findMissingTags(script, doc).isEmpty
    }

    internal fun applyTo(
        project: Project,
        document: Document,
        script: RsScript,
    ) {
        val doc = script.findDoc() ?: return
        val missing = findMissingTags(script, doc)
        if (missing.isEmpty) return
        document.replaceString(doc.textRange.startOffset, doc.textRange.endOffset, addTags(doc.text, missing))
        PsiDocumentManager.getInstance(project).commitDocument(document)
    }

    private fun findMissingTags(
        script: RsScript,
        doc: RsDoc,
    ): MissingTags {
        val existingParameters =
            doc
                .getAllSections()
                .flatMap { it.findTagsByName("param") }
                .mapNotNullTo(mutableSetOf()) { it.getSubjectName() }
        val signatureParameters =
            script.parameterList
                ?.parameterList
                ?.mapNotNull { it.localVariableExpression?.nameLiteral?.text }
                .orEmpty()
        val missingParameters = signatureParameters.filterNot { it in existingParameters }
        val existingReturns = doc.getAllSections().sumOf { it.findTagsByName("return").size }
        val returnCount = script.returnList?.typeNameList?.size ?: 0
        return MissingTags(signatureParameters, missingParameters, (returnCount - existingReturns).coerceAtLeast(0))
    }

    private fun addTags(
        text: String,
        missing: MissingTags,
    ): String {
        val lines = normalizeComment(text)
        missing.parameters.forEach { parameter ->
            val parameterIndex = missing.signatureParameters.indexOf(parameter)
            val insertionIndex =
                lines
                    .indexOfFirst { line ->
                        val existing = PARAMETER_TAG.matchEntire(line)?.groupValues?.get(1) ?: return@indexOfFirst false
                        val existingIndex = missing.signatureParameters.indexOf(existing)
                        existingIndex >= 0 && existingIndex > parameterIndex
                    }.takeIf { it >= 0 }
                    ?: lines.indexOfFirst { TAG_START.matches(it) && PARAMETER_TAG.matchEntire(it) == null }.takeIf { it >= 0 }
                    ?: lines.lastIndex
            lines.add(insertionIndex, " * @param $parameter")
        }
        var returnIndex =
            lines
                .indexOfFirst { line ->
                    val tag = TAG_START.matchEntire(line)?.groupValues?.get(1) ?: return@indexOfFirst false
                    tag !in setOf("param", "parammeta", "return")
                }.takeIf { it >= 0 } ?: lines.lastIndex
        repeat(missing.returnCount) {
            lines.add(returnIndex++, " * @return")
        }
        return lines.joinToString("\n")
    }

    private fun normalizeComment(text: String): MutableList<String> {
        if ('\n' in text) return text.lines().toMutableList()

        val description = text.removePrefix("/**").removeSuffix("*/").trim()
        return buildList {
            add("/**")
            if (description.isNotEmpty()) {
                add(" * $description")
                add(" *")
            }
            add(" */")
        }.toMutableList()
    }

    private data class MissingTags(
        val signatureParameters: List<String>,
        val parameters: List<String>,
        val returnCount: Int,
    ) {
        val isEmpty: Boolean
            get() = parameters.isEmpty() && returnCount == 0
    }

    companion object {
        private val PARAMETER_TAG = Regex("""\s*\*\s*@param(?:meta)?\s+(\S+)(?:\s.*)?""")
        private val TAG_START = Regex("""\s*\*\s*@(\S+)(?:\s.*)?""")
    }
}

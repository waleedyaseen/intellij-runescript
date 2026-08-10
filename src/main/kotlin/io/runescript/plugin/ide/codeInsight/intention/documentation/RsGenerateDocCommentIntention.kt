package io.runescript.plugin.ide.codeInsight.intention.documentation

import com.intellij.codeInsight.intention.BaseElementAtCaretIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.ide.codeInsight.intention.findScriptAtSignature
import io.runescript.plugin.ide.doc.findDoc
import io.runescript.plugin.lang.psi.RsScript

class RsGenerateDocCommentIntention : BaseElementAtCaretIntentionAction() {
    override fun getFamilyName(): String = RsBundle.message("intention.generate.rsdoc.family.name")

    override fun getText(): String = RsBundle.message("intention.generate.rsdoc.name")

    override fun invoke(
        project: Project,
        editor: Editor,
        element: PsiElement,
    ) {
        val script = element.findScriptAtSignature() ?: return
        if (script.findDoc() != null) return

        val comment = buildComment(script)
        val offset = script.textRange.startOffset
        editor.document.insertString(offset, "$comment\n")
        PsiDocumentManager.getInstance(project).commitDocument(editor.document)
        editor.caretModel.moveToOffset(offset + DESCRIPTION_OFFSET)
    }

    override fun isAvailable(
        project: Project,
        editor: Editor,
        element: PsiElement,
    ): Boolean {
        val script = element.findScriptAtSignature() ?: return false
        return script.findDoc() == null
    }

    private fun buildComment(script: RsScript): String {
        val tags = mutableListOf<String>()
        script.parameterList
            ?.parameterList
            ?.mapNotNull { it.localVariableExpression?.nameLiteral?.text }
            ?.mapTo(tags) { "@param $it" }
        repeat(script.returnList?.typeNameList?.size ?: 0) {
            tags += "@return"
        }

        return buildString {
            appendLine("/**")
            append(" * ")
            if (tags.isNotEmpty()) {
                appendLine()
                appendLine(" *")
                tags.forEach { appendLine(" * $it") }
                append(" */")
            } else {
                appendLine()
                append(" */")
            }
        }
    }

    companion object {
        private const val DESCRIPTION_OFFSET = 7
    }
}

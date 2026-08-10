package io.runescript.plugin.ide.codeInsight.intention.controlFlow

import com.intellij.codeInsight.intention.BaseElementAtCaretIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleManager
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.lang.psi.RsElementGenerator
import io.runescript.plugin.lang.psi.RsIfStatement

class RsAddElseBranchIntention : BaseElementAtCaretIntentionAction() {
    override fun getFamilyName(): String = RsBundle.message("intention.add.else.branch.family.name")

    override fun getText(): String = RsBundle.message("intention.add.else.branch.name")

    override fun invoke(
        project: Project,
        editor: Editor,
        element: PsiElement,
    ) {
        val ifStatement = findIf(element) ?: return
        val file = ifStatement.containingFile
        val ifOffset = ifStatement.textOffset
        val replacement = RsElementGenerator.createStatement(project, "${ifStatement.text} else {\n;\n}")
        CodeStyleManager.getInstance(project).reformat(ifStatement.replace(replacement))
        val documentManager = PsiDocumentManager.getInstance(project)
        documentManager.doPostponedOperationsAndUnblockDocument(editor.document)
        documentManager.commitDocument(editor.document)

        val replaced = file.findElementAt(ifOffset)?.parent as? RsIfStatement ?: return
        val falseBranch = replaced.falseStatement ?: return
        val placeholderOffset = falseBranch.textRange.startOffset + falseBranch.text.indexOf(';')
        if (placeholderOffset >= 0) {
            editor.document.deleteString(placeholderOffset, placeholderOffset + 1)
            editor.caretModel.moveToOffset(placeholderOffset)
        }
    }

    override fun isAvailable(
        project: Project,
        editor: Editor,
        element: PsiElement,
    ): Boolean = findIf(element)?.let { it.trueStatement != null && it.falseStatement == null } == true

    private fun findIf(element: PsiElement): RsIfStatement? {
        val ifStatement = element.parent as? RsIfStatement ?: return null
        return ifStatement.takeIf { element == it.`if` }
    }
}

package io.runescript.plugin.ide.codeInsight.intention.controlFlow

import com.intellij.codeInsight.intention.BaseElementAtCaretIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleManager
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.lang.psi.RsElementGenerator
import io.runescript.plugin.lang.psi.RsSwitchCase
import io.runescript.plugin.lang.psi.RsSwitchCaseDefaultExpression
import io.runescript.plugin.lang.psi.RsSwitchStatement

class RsMoveSwitchDefaultLastIntention : BaseElementAtCaretIntentionAction() {
    override fun getFamilyName(): String = RsBundle.message("intention.move.switch.default.last.family.name")

    override fun getText(): String = RsBundle.message("intention.move.switch.default.last.name")

    override fun invoke(
        project: Project,
        editor: Editor,
        element: PsiElement,
    ) {
        val defaultCase = findDefaultCase(element) ?: return
        val switch = defaultCase.parent as? RsSwitchStatement ?: return
        val file = switch.containingFile
        val switchOffset = switch.textOffset
        val cases = switch.switchCaseList
        val orderedCases = cases.filterNot { it == defaultCase } + defaultCase
        val replacement = RsElementGenerator.createStatement(project, switch.textWithCases(orderedCases) ?: return)
        CodeStyleManager.getInstance(project).reformat(switch.replace(replacement))
        val documentManager = PsiDocumentManager.getInstance(project)
        documentManager.doPostponedOperationsAndUnblockDocument(editor.document)
        documentManager.commitDocument(editor.document)

        val replaced = file.findElementAt(switchOffset)?.parent as? RsSwitchStatement ?: return
        val defaultExpression =
            replaced.switchCaseList
                .lastOrNull()
                ?.expressionList
                ?.filterIsInstance<RsSwitchCaseDefaultExpression>()
                ?.singleOrNull()
                ?: return
        editor.caretModel.moveToOffset(defaultExpression.textOffset)
    }

    override fun isAvailable(
        project: Project,
        editor: Editor,
        element: PsiElement,
    ): Boolean {
        val defaultCase = findDefaultCase(element) ?: return false
        val switch = defaultCase.parent as? RsSwitchStatement ?: return false
        return switch.rbrace != null && switch.switchCaseList.lastOrNull() != defaultCase
    }

    private fun findDefaultCase(element: PsiElement): RsSwitchCase? {
        val expression = element.parent as? RsSwitchCaseDefaultExpression ?: return null
        if (element != expression.`default`) return null
        return expression.parent as? RsSwitchCase
    }
}

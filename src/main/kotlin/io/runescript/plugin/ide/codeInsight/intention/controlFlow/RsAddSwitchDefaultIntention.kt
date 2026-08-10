package io.runescript.plugin.ide.codeInsight.intention.controlFlow

import com.intellij.codeInsight.intention.BaseElementAtCaretIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.lang.psi.RsElementGenerator
import io.runescript.plugin.lang.psi.RsEmptyStatement
import io.runescript.plugin.lang.psi.RsSwitchCaseDefaultExpression
import io.runescript.plugin.lang.psi.RsSwitchStatement

class RsAddSwitchDefaultIntention : BaseElementAtCaretIntentionAction() {
    override fun getFamilyName(): String = RsBundle.message("intention.add.switch.default.family.name")

    override fun getText(): String = RsBundle.message("intention.add.switch.default.name")

    override fun invoke(
        project: Project,
        editor: Editor,
        element: PsiElement,
    ) {
        val switch = findSwitch(element) ?: return
        val file = switch.containingFile
        val switchOffset = switch.textOffset
        val rbrace = switch.rbrace ?: return
        val insertionOffset = rbrace.textOffset - switch.textOffset
        val prefix = switch.text.substring(0, insertionOffset).trimEnd()
        val replacementText = "$prefix\ncase default :\n;\n${switch.text.substring(insertionOffset)}"
        val replacement = RsElementGenerator.createStatement(project, replacementText) as RsSwitchStatement
        CodeStyleManager.getInstance(project).reformat(switch.replace(replacement))
        val documentManager = PsiDocumentManager.getInstance(project)
        documentManager.doPostponedOperationsAndUnblockDocument(editor.document)
        documentManager.commitDocument(editor.document)
        val replaced = file.findElementAt(switchOffset)?.parent as? RsSwitchStatement ?: return
        val defaultCase =
            replaced.switchCaseList.lastOrNull()?.takeIf { case ->
                case.expressionList.any { it is RsSwitchCaseDefaultExpression }
            } ?: return
        val placeholderOffset = PsiTreeUtil.findChildOfType(defaultCase, RsEmptyStatement::class.java)?.textOffset ?: return
        editor.document.deleteString(placeholderOffset, placeholderOffset + 1)
        editor.caretModel.moveToOffset(placeholderOffset)
    }

    override fun isAvailable(
        project: Project,
        editor: Editor,
        element: PsiElement,
    ): Boolean = findSwitch(element)?.let { it.rbrace != null && !it.hasDefaultCase() } == true

    private fun findSwitch(element: PsiElement): RsSwitchStatement? {
        val switch = element.parent as? RsSwitchStatement ?: return null
        return switch.takeIf { element == it.`switch` }
    }

    private fun RsSwitchStatement.hasDefaultCase(): Boolean =
        switchCaseList.any { case -> case.expressionList.any { it is RsSwitchCaseDefaultExpression } }
}

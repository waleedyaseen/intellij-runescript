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

class RsAddSwitchCaseIntention : BaseElementAtCaretIntentionAction() {
    override fun getFamilyName(): String = RsBundle.message("intention.add.switch.case.family.name")

    override fun getText(): String = RsBundle.message("intention.add.switch.case.name")

    override fun invoke(
        project: Project,
        editor: Editor,
        element: PsiElement,
    ) {
        val switch = findSwitch(element) ?: return
        val file = switch.containingFile
        val switchOffset = switch.textOffset
        val rbrace = switch.rbrace ?: return
        val defaultCase =
            switch.switchCaseList.firstOrNull { case ->
                case.expressionList.any { it is RsSwitchCaseDefaultExpression }
            }
        val insertionIndex = defaultCase?.let(switch.switchCaseList::indexOf) ?: switch.switchCaseList.size
        val placeholder = switch.nextPlaceholder()
        val insertionOffset = (defaultCase?.textOffset ?: rbrace.textOffset) - switch.textOffset
        val prefix = switch.text.substring(0, insertionOffset).trimEnd()
        val suffix = switch.text.substring(insertionOffset).trimStart()
        val replacementText = "$prefix\ncase $placeholder :\n;\n$suffix"
        val replacement = RsElementGenerator.createStatement(project, replacementText) as RsSwitchStatement
        CodeStyleManager.getInstance(project).reformat(switch.replace(replacement))
        val documentManager = PsiDocumentManager.getInstance(project)
        documentManager.doPostponedOperationsAndUnblockDocument(editor.document)
        documentManager.commitDocument(editor.document)

        val replaced = file.findElementAt(switchOffset)?.parent as? RsSwitchStatement ?: return
        val insertedCase = replaced.switchCaseList.getOrNull(insertionIndex) ?: return
        val insertedValue = insertedCase.expressionList.singleOrNull() ?: return
        val valueRange = insertedValue.textRange
        val placeholderOffset = PsiTreeUtil.findChildOfType(insertedCase, RsEmptyStatement::class.java)?.textOffset
        if (placeholderOffset != null) {
            editor.document.deleteString(placeholderOffset, placeholderOffset + 1)
            editor.selectionModel.setSelection(valueRange.startOffset, valueRange.endOffset)
            editor.caretModel.moveToOffset(valueRange.endOffset)
        }
    }

    override fun isAvailable(
        project: Project,
        editor: Editor,
        element: PsiElement,
    ): Boolean = findSwitch(element)?.rbrace != null

    private fun findSwitch(element: PsiElement): RsSwitchStatement? {
        val switch = element.parent as? RsSwitchStatement ?: return null
        return switch.takeIf { element == it.`switch` }
    }

    private fun RsSwitchStatement.nextPlaceholder(): String {
        val used = switchCaseList.flatMap { it.expressionList }.mapTo(mutableSetOf()) { it.text }
        return when (switch.text.removePrefix("switch_")) {
            "boolean" -> listOf("false", "true").firstOrNull { it !in used } ?: "false"
            "coord" -> generateSequence(0) { it + 1 }.map { "0_0_0_0_$it" }.first { it !in used }
            else -> generateSequence(0) { it + 1 }.map(Int::toString).first { it !in used }
        }
    }
}

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

class RsMergeSwitchCasesIntention : BaseElementAtCaretIntentionAction() {
    override fun getFamilyName(): String = RsBundle.message("intention.merge.switch.cases.family.name")

    override fun getText(): String = RsBundle.message("intention.merge.switch.cases.name")

    override fun invoke(
        project: Project,
        editor: Editor,
        element: PsiElement,
    ) {
        val (case, nextCase) = findCases(element) ?: return
        val switch = case.parent as? RsSwitchStatement ?: return
        val values = (case.expressionList + nextCase.expressionList).joinToString(", ") { it.text }
        val caseOffset = case.textOffset - switch.textOffset
        val betweenCases =
            switch.text
                .substring(case.textRange.endOffset - switch.textOffset, nextCase.textOffset - switch.textOffset)
                .trim()
        val preservedTrivia = betweenCases.takeIf { it.isNotEmpty() }?.let { "$it\n" }.orEmpty()
        val mergedCaseText = "case $values :\n$preservedTrivia${case.statementList.text.trim()}"
        val nextCaseEndOffset = nextCase.textRange.endOffset - switch.textOffset
        val replacementText =
            switch.text.substring(0, caseOffset) + mergedCaseText + switch.text.substring(nextCaseEndOffset)
        val replacement = RsElementGenerator.createStatement(project, replacementText)
        val switchOffset = switch.textOffset
        CodeStyleManager.getInstance(project).reformat(switch.replace(replacement))
        PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.document)
        val caseKeywordOffset = editor.document.text.indexOf("case ", switchOffset + caseOffset)
        if (caseKeywordOffset >= 0) editor.caretModel.moveToOffset(caseKeywordOffset)
    }

    override fun isAvailable(
        project: Project,
        editor: Editor,
        element: PsiElement,
    ): Boolean = findCases(element) != null

    private fun findCases(element: PsiElement): Pair<RsSwitchCase, RsSwitchCase>? {
        val case = element.parent as? RsSwitchCase ?: return null
        if (element != case.`case` || case.hasDefault()) return null
        val switch = case.parent as? RsSwitchStatement ?: return null
        val index = switch.switchCaseList.indexOf(case)
        val nextCase = switch.switchCaseList.getOrNull(index + 1) ?: return null
        if (nextCase.hasDefault()) return null
        if (case.statementList.text.trim() != nextCase.statementList.text.trim()) return null
        return case to nextCase
    }

    private fun RsSwitchCase.hasDefault(): Boolean = expressionList.any { it is RsSwitchCaseDefaultExpression }
}

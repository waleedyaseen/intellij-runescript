package io.runescript.plugin.ide.codeInsight.intention.controlFlow

import com.intellij.codeInsight.intention.BaseElementAtCaretIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleManager
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.lang.psi.RsElementGenerator
import io.runescript.plugin.lang.psi.RsSwitchCase
import io.runescript.plugin.lang.psi.RsSwitchCaseDefaultExpression
import io.runescript.plugin.lang.psi.RsSwitchStatement

class RsMoveSwitchCaseUpIntention : RsMoveSwitchCaseIntention(-1) {
    override fun getFamilyName(): String = RsBundle.message("intention.move.switch.case.family.name")

    override fun getText(): String = RsBundle.message("intention.move.switch.case.up.name")
}

class RsMoveSwitchCaseDownIntention : RsMoveSwitchCaseIntention(1) {
    override fun getFamilyName(): String = RsBundle.message("intention.move.switch.case.family.name")

    override fun getText(): String = RsBundle.message("intention.move.switch.case.down.name")
}

abstract class RsMoveSwitchCaseIntention(
    private val direction: Int,
) : BaseElementAtCaretIntentionAction() {
    override fun invoke(
        project: Project,
        editor: Editor,
        element: PsiElement,
    ) {
        val target = findTarget(element) ?: return
        val cases = target.switch.switchCaseList.toMutableList()
        val movedCase = cases.removeAt(target.index)
        cases.add(target.index + direction, movedCase)

        val replacement = RsElementGenerator.createStatement(project, target.switch.textWithCases(cases) ?: return)
        CodeStyleManager.getInstance(project).reformat(target.switch.replace(replacement))
    }

    override fun isAvailable(
        project: Project,
        editor: Editor,
        element: PsiElement,
    ): Boolean = findTarget(element) != null

    private fun findTarget(element: PsiElement): Target? {
        val case = element.parent as? RsSwitchCase ?: return null
        if (element != case.`case` || case.hasDefaultLabel()) return null
        val switch = case.parent as? RsSwitchStatement ?: return null
        if (switch.rbrace == null) return null
        val cases = switch.switchCaseList
        val defaultIndexes = cases.indices.filter { cases[it].hasDefaultLabel() }
        if (defaultIndexes.size > 1 || defaultIndexes.singleOrNull()?.let { it != cases.lastIndex } == true) return null

        val index = cases.indexOf(case)
        val destination = index + direction
        if (destination !in cases.indices || cases[destination].hasDefaultLabel()) return null
        return Target(switch, index)
    }

    private fun RsSwitchCase.hasDefaultLabel(): Boolean = expressionList.any { it is RsSwitchCaseDefaultExpression }

    private data class Target(
        val switch: RsSwitchStatement,
        val index: Int,
    )
}

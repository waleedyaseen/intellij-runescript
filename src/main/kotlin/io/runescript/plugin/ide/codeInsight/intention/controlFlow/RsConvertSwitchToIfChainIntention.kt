package io.runescript.plugin.ide.codeInsight.intention.controlFlow

import com.intellij.codeInsight.intention.BaseElementAtCaretIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleManager
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.lang.psi.RsElementGenerator
import io.runescript.plugin.lang.psi.RsExpression
import io.runescript.plugin.lang.psi.RsLocalVariableExpression
import io.runescript.plugin.lang.psi.RsParExpression
import io.runescript.plugin.lang.psi.RsScopedVariableExpression
import io.runescript.plugin.lang.psi.RsSwitchCase
import io.runescript.plugin.lang.psi.RsSwitchCaseDefaultExpression
import io.runescript.plugin.lang.psi.RsSwitchStatement

class RsConvertSwitchToIfChainIntention : BaseElementAtCaretIntentionAction() {
    override fun getFamilyName(): String = RsBundle.message("intention.convert.switch.to.if.chain.family.name")

    override fun getText(): String = RsBundle.message("intention.convert.switch.to.if.chain.name")

    override fun invoke(
        project: Project,
        editor: Editor,
        element: PsiElement,
    ) {
        val conversion = findConversion(element) ?: return
        val replacement = RsElementGenerator.createStatement(project, conversion.toSource())
        CodeStyleManager.getInstance(project).reformat(conversion.switch.replace(replacement))
    }

    override fun isAvailable(
        project: Project,
        editor: Editor,
        element: PsiElement,
    ): Boolean = findConversion(element) != null

    private fun findConversion(element: PsiElement): Conversion? {
        val switch = element.parent as? RsSwitchStatement ?: return null
        if (element != switch.switch) return null
        val selector = switch.expression ?: return null
        if (!selector.isStableSelector()) return null

        val cases = switch.switchCaseList
        val defaultIndexes = cases.indices.filter { cases[it].hasDefaultLabel() }
        if (defaultIndexes.size > 1 || defaultIndexes.singleOrNull()?.let { it != cases.lastIndex } == true) return null

        val branches = cases.filterNot { it.hasDefaultLabel() }
        if (branches.isEmpty() || branches.any { it.expressionList.isEmpty() }) return null
        return Conversion(switch, selector, branches, cases.lastOrNull()?.takeIf { it.hasDefaultLabel() })
    }

    private fun RsExpression.isStableSelector(): Boolean =
        when (this) {
            is RsLocalVariableExpression, is RsScopedVariableExpression -> true
            is RsParExpression -> expression.isStableSelector()
            else -> false
        }

    private fun Conversion.toSource(): String =
        buildString {
            branches.forEachIndexed { index, branch ->
                if (index > 0) append(" else ")
                append("if (")
                append(branch.conditionText(selector))
                append(") {\n")
                append(branch.statementList.text.trim())
                append("\n}")
            }
            defaultCase?.let {
                append(" else {\n")
                append(it.statementList.text.trim())
                append("\n}")
            }
        }

    private fun RsSwitchCase.conditionText(selector: RsExpression): String =
        expressionList.joinToString(" | ") { value -> "(${selector.text} = ${value.text})" }

    private fun RsSwitchCase.hasDefaultLabel(): Boolean = expressionList.any { it is RsSwitchCaseDefaultExpression }

    private data class Conversion(
        val switch: RsSwitchStatement,
        val selector: RsExpression,
        val branches: List<RsSwitchCase>,
        val defaultCase: RsSwitchCase?,
    )
}

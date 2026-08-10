package io.runescript.plugin.ide.codeInsight.intention.controlFlow

import com.intellij.codeInsight.intention.BaseElementAtCaretIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleManager
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.lang.psi.RsBlockStatement
import io.runescript.plugin.lang.psi.RsBooleanLiteralExpression
import io.runescript.plugin.lang.psi.RsConditionExpression
import io.runescript.plugin.lang.psi.RsCoordLiteralExpression
import io.runescript.plugin.lang.psi.RsElementGenerator
import io.runescript.plugin.lang.psi.RsExpression
import io.runescript.plugin.lang.psi.RsIfStatement
import io.runescript.plugin.lang.psi.RsIntegerLiteralExpression
import io.runescript.plugin.lang.psi.RsLocalVariableExpression
import io.runescript.plugin.lang.psi.RsParExpression
import io.runescript.plugin.lang.psi.RsRelationalValueExpression
import io.runescript.plugin.lang.psi.RsScopedVariableExpression
import io.runescript.plugin.lang.psi.RsStatement

class RsConvertIfChainToSwitchIntention : BaseElementAtCaretIntentionAction() {
    override fun getFamilyName(): String = RsBundle.message("intention.convert.if.chain.to.switch.family.name")

    override fun getText(): String = RsBundle.message("intention.convert.if.chain.to.switch.name")

    override fun invoke(
        project: Project,
        editor: Editor,
        element: PsiElement,
    ) {
        val conversion = findConversion(element) ?: return
        val caseText =
            conversion.branches.joinToString("\n") { branch ->
                "case ${branch.value.text} :\n${branch.body.bodyText()}"
            }
        val defaultText = conversion.defaultBody?.let { "\ncase default :\n${it.bodyText()}" }.orEmpty()
        val replacement =
            RsElementGenerator.createStatement(
                project,
                "switch_${conversion.switchType} (${conversion.selector.text}) {\n$caseText$defaultText\n}",
            )
        CodeStyleManager.getInstance(project).reformat(conversion.root.replace(replacement))
    }

    override fun isAvailable(
        project: Project,
        editor: Editor,
        element: PsiElement,
    ): Boolean = findConversion(element) != null

    private fun findConversion(element: PsiElement): Conversion? {
        val root = element.parent as? RsIfStatement ?: return null
        if (element != root.`if`) return null
        val parentIf = root.parent as? RsIfStatement
        if (parentIf?.falseStatement == root) return null

        val branches = mutableListOf<Branch>()
        var current: RsIfStatement? = root
        var defaultBody: RsStatement? = null
        var selector: RsExpression? = null
        var switchType: String? = null
        while (current != null) {
            val match = current.expression?.toEqualityMatch() ?: return null
            val body = current.trueStatement ?: return null
            if (selector == null) {
                selector = match.selector
                switchType = match.switchType
            } else if (selector.text != match.selector.text || switchType != match.switchType) {
                return null
            }
            branches += Branch(match.value, body)
            when (val falseStatement = current.falseStatement) {
                is RsIfStatement -> {
                    current = falseStatement
                }

                null -> {
                    current = null
                }

                else -> {
                    defaultBody = falseStatement
                    current = null
                }
            }
        }
        if (branches.size < 2 || branches.map { it.value.caseKey() }.toSet().size != branches.size) return null
        return Conversion(root, selector ?: return null, switchType ?: return null, branches, defaultBody)
    }

    private fun RsExpression.toEqualityMatch(): EqualityMatch? {
        val condition = unwrapParentheses() as? RsConditionExpression ?: return null
        if (condition.conditionOp.text != "=") return null
        val right = condition.right ?: return null
        return matchSelectorAndValue(condition.left, right) ?: matchSelectorAndValue(right, condition.left)
    }

    private fun matchSelectorAndValue(
        selector: RsExpression,
        value: RsExpression,
    ): EqualityMatch? {
        val unwrappedSelector = selector.unwrapParentheses()
        val unwrappedValue = value.unwrapParentheses()
        if (unwrappedSelector !is RsLocalVariableExpression && unwrappedSelector !is RsScopedVariableExpression) return null
        val switchType = unwrappedValue.switchType() ?: return null
        return EqualityMatch(unwrappedSelector, unwrappedValue, switchType)
    }

    private tailrec fun RsExpression.unwrapParentheses(): RsExpression =
        when (this) {
            is RsParExpression -> expression.unwrapParentheses()
            is RsRelationalValueExpression -> expression?.unwrapParentheses() ?: this
            else -> this
        }

    private fun RsExpression.switchType(): String? =
        when (this) {
            is RsIntegerLiteralExpression -> "int"
            is RsCoordLiteralExpression -> "coord"
            is RsBooleanLiteralExpression -> "boolean"
            else -> null
        }

    private fun RsExpression.caseKey(): Any =
        when (this) {
            is RsIntegerLiteralExpression -> parseIntegerKey(text) ?: text
            else -> text
        }

    private fun parseIntegerKey(text: String): Int? {
        val negative = text.startsWith('-')
        var digits = text.removePrefix("-").removePrefix("+")
        val radix = if (digits.startsWith("0x", ignoreCase = true)) 16 else 10
        if (radix == 16) digits = digits.substring(2)
        val value = digits.toUIntOrNull(radix)?.toInt() ?: return null
        return if (negative) -value else value
    }

    private fun RsStatement.bodyText(): String = if (this is RsBlockStatement) statementList.text.trim() else text

    private data class EqualityMatch(
        val selector: RsExpression,
        val value: RsExpression,
        val switchType: String,
    )

    private data class Branch(
        val value: RsExpression,
        val body: RsStatement,
    )

    private data class Conversion(
        val root: RsIfStatement,
        val selector: RsExpression,
        val switchType: String,
        val branches: List<Branch>,
        val defaultBody: RsStatement?,
    )
}

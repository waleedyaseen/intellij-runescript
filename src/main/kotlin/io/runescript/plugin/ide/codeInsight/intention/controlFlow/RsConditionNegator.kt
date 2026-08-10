package io.runescript.plugin.ide.codeInsight.intention.controlFlow

import io.runescript.plugin.lang.psi.RsConditionExpression
import io.runescript.plugin.lang.psi.RsExpression
import io.runescript.plugin.lang.psi.RsParExpression
import io.runescript.plugin.lang.psi.RsRelationalValueExpression
import io.runescript.plugin.lang.psi.isLogicalAnd
import io.runescript.plugin.lang.psi.isLogicalOr

object RsConditionNegator {
    fun negate(expression: RsExpression): String? =
        when (expression) {
            is RsParExpression -> {
                negate(expression.expression)?.let { "($it)" }
            }

            is RsRelationalValueExpression -> {
                expression.expression
                    ?.takeIf { expression.lparen != null }
                    ?.let(::negate)
                    ?.let { "($it)" }
            }

            is RsConditionExpression -> {
                negateCondition(expression)
            }

            else -> {
                null
            }
        }

    private fun negateCondition(expression: RsConditionExpression): String? {
        val right = expression.right ?: return null
        val operator = expression.conditionOp
        if (operator.isLogicalAnd() || operator.isLogicalOr()) {
            val leftText = negate(expression.left) ?: return null
            val rightText = negate(right) ?: return null
            val negatedOperator = if (operator.isLogicalAnd()) "|" else "&"
            return "($leftText) $negatedOperator ($rightText)"
        }

        val negatedOperator =
            when (operator.text) {
                "=" -> "!"
                "!" -> "="
                ">" -> "<="
                ">=" -> "<"
                "<" -> ">="
                "<=" -> ">"
                else -> return null
            }
        return "${expression.left.text} $negatedOperator ${right.text}"
    }
}

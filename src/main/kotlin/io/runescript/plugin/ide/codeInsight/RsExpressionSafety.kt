package io.runescript.plugin.ide.codeInsight

import io.runescript.plugin.lang.psi.RsArrayAccessExpression
import io.runescript.plugin.lang.psi.RsBinaryExpression
import io.runescript.plugin.lang.psi.RsBooleanLiteralExpression
import io.runescript.plugin.lang.psi.RsCalcExpression
import io.runescript.plugin.lang.psi.RsConstantExpression
import io.runescript.plugin.lang.psi.RsCoordLiteralExpression
import io.runescript.plugin.lang.psi.RsDynamicExpression
import io.runescript.plugin.lang.psi.RsExpression
import io.runescript.plugin.lang.psi.RsIntegerLiteralExpression
import io.runescript.plugin.lang.psi.RsLocalVariableExpression
import io.runescript.plugin.lang.psi.RsLongLiteralExpression
import io.runescript.plugin.lang.psi.RsNullLiteralExpression
import io.runescript.plugin.lang.psi.RsParExpression
import io.runescript.plugin.lang.psi.RsStringInterpolationExpression
import io.runescript.plugin.lang.psi.RsStringLiteralExpression
import io.runescript.plugin.symbollang.psi.RsSymSymbol

internal fun RsExpression.isSafeToDiscard(): Boolean = hasSafeEvaluation(EvaluationUse.DISCARD)

internal fun RsExpression.isStableForDuplication(): Boolean = hasSafeEvaluation(EvaluationUse.DUPLICATE)

private fun RsExpression.hasSafeEvaluation(use: EvaluationUse): Boolean {
    if (this is RsStringLiteralExpression) {
        return stringLiteralContent.stringInterpolationExpressionList.all { expression ->
            expression.hasSafeEvaluation(use)
        }
    }
    return when (this) {
        is RsNullLiteralExpression,
        is RsIntegerLiteralExpression,
        is RsCoordLiteralExpression,
        is RsBooleanLiteralExpression,
        is RsLongLiteralExpression,
        is RsConstantExpression,
        -> true

        is RsLocalVariableExpression,
        is RsArrayAccessExpression,
        -> use == EvaluationUse.DISCARD

        is RsStringInterpolationExpression -> expression.hasSafeEvaluation(use)

        is RsParExpression -> expression.hasSafeEvaluation(use)

        is RsCalcExpression -> expression.hasSafeEvaluation(use)

        is RsBinaryExpression -> left.hasSafeEvaluation(use) && right.hasSafeEvaluation(use)

        is RsDynamicExpression -> reference?.resolve() is RsSymSymbol

        else -> false
    }
}

private enum class EvaluationUse {
    DISCARD,
    DUPLICATE,
}

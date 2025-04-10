package io.runescript.plugin.ide.codeInsight

import com.intellij.codeInsight.hints.InlayInfo
import com.intellij.codeInsight.hints.InlayParameterHintsProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.util.startOffset
import io.runescript.plugin.lang.psi.*

@Suppress("UnstableApiUsage")
class RsInlayParameterHintsProvider : InlayParameterHintsProvider {

    override fun getDefaultBlackList(): Set<String> = emptySet()

    override fun getParameterHints(element: PsiElement): List<InlayInfo> {
        val argumentsList = getArgumentsList(element) ?: return emptyList()
        val parametersList = getParametersList(element) ?: return emptyList()
        val inlayHints = mutableListOf<InlayInfo>()
        var parameterIndex = 0
        for (argument in argumentsList) {
            if (parameterIndex >= parametersList.size) {
                break
            }
            val parameter = parametersList[parameterIndex]
            if (!shouldHideHint(parameter, argument)) {
                inlayHints += InlayInfo(parameter, argument.startOffset,
                        isShowOnlyIfExistedBefore = false,
                        isFilterByBlacklist = false,
                        relatesToPrecedingText = false
                )
            }
            parameterIndex += guesstimateReturnCount(argument)
        }
        return inlayHints
    }

    private fun shouldHideHint(parameterName: String, argument: RsExpression): Boolean {
        val argumentName = when (argument) {
            is RsConstantExpression -> argument.nameLiteral.text
            is RsGosubExpression -> argument.nameLiteral.text
            is RsCommandExpression -> argument.nameLiteral.text
            is RsLocalVariableExpression -> argument.nameLiteral.text
            is RsScopedVariableExpression -> argument.nameLiteral.text
            is RsArrayAccessExpression -> (argument.expressionList[0] as RsLocalVariableExpression).nameLiteral.text
            else -> null
        }
        if (parameterName.length < 3) {
            return true
        }
        if (argumentName != null) {
            if (argumentName.length < 3) {
                return true
            }
            val argName = argumentName.lowercase()
            val paramName = parameterName.lowercase()
            if (paramName.contains(argName) || argName.contains(paramName)) {
                return true
            }
        }
        var unwrappedExpression = argument
        while (unwrappedExpression is RsParExpression) {
            unwrappedExpression = unwrappedExpression.expression
        }
        return unwrappedExpression !is RsIntegerLiteralExpression
                && unwrappedExpression !is RsLongLiteralExpression
                && unwrappedExpression !is RsBooleanLiteralExpression
                && unwrappedExpression !is RsStringLiteralExpression
                && unwrappedExpression !is RsNullLiteralExpression
    }



    private fun getArgumentsList(element: PsiElement) = when (element) {
        is RsGosubExpression -> element.argumentList?.expressionList
        is RsCommandExpression -> element.argumentList.expressionList
        else -> null
    }

    private fun getParametersList(element: PsiElement): List<String>? {
        return when (element) {
            is RsGosubExpression, is RsCommandExpression -> {
                val reference = element.reference?.resolve() ?: return null
                reference as RsScript
                reference.parameterList?.parameterList?.map { it.localVariableExpression?.name ?: "<unknown-parameter>" }
            }

            else -> return null
        }
    }

    private fun guesstimateReturnCount(element: PsiElement) = when (element) {
        is RsGosubExpression -> {
            val reference = element.reference?.resolve()
            if (reference == null) {
                1
            } else {
                reference as RsScript
                reference.returnList?.typeNameList?.size ?: 0
            }
        }

        is RsCommandExpression -> {
            1
        }

        else -> {
            1
        }
    }
}
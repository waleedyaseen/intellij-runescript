package io.runescript.plugin.ide.codeInsight

import com.intellij.codeInsight.hints.InlayInfo
import com.intellij.codeInsight.hints.InlayParameterHintsProvider
import com.intellij.psi.PsiElement
import com.intellij.refactoring.suggested.startOffset
import io.runescript.plugin.lang.psi.RsCommandExpression
import io.runescript.plugin.lang.psi.RsGosubExpression
import io.runescript.plugin.lang.psi.RsScriptHeader

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
            inlayHints += InlayInfo(parameter, argument.startOffset)
            parameterIndex += guesstimateReturnCount(argument)
        }
        return inlayHints
    }

    private fun getArgumentsList(element: PsiElement) = when (element) {
        is RsGosubExpression -> element.argumentList?.expressionList
        is RsCommandExpression -> element.argumentList?.expressionList
        else -> null
    }

    private fun getParametersList(element: PsiElement): List<String>? {
        return when (element) {
            is RsGosubExpression -> {
                val reference = element.reference?.resolve() ?: return null
                reference as RsScriptHeader
                reference.parameterList?.parameterList?.map { it.localVariableExpression.variableName }
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
                reference as RsScriptHeader
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
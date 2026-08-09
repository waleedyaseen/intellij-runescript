package io.runescript.plugin.lang.psi

import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import io.runescript.plugin.ide.neptune.neptuneModuleData
import io.runescript.plugin.ide.neptune.typeManager
import io.runescript.plugin.lang.RuneScript
import io.runescript.plugin.lang.psi.typechecker.type.MetaType
import io.runescript.plugin.symbollang.RuneScriptSymbol

fun RsStringLiteralContent.isBasicContent(): Boolean {
    val first = node.firstChildNode ?: return true
    return first == node.lastChildNode && first.elementType == RsElementTypes.STRING_PART
}

fun RsStringLiteralContent.isHookExpression(): Boolean =
    CachedValuesManager.getCachedValue(this) {
        val languageModifications =
            PsiModificationTracker
                .getInstance(project)
                .forLanguages { it == RuneScript || it == RuneScriptSymbol }
        val dependencies = mutableListOf<Any>(languageModifications)
        neptuneModuleData?.let(dependencies::add)
        val argument = parent
        if (argument is RsStringLiteralExpression) {
            val argumentList = argument.parent
            if (argumentList is RsArgumentList) {
                val commandExpr = argumentList.parent
                if (commandExpr is RsCommandExpression) {
                    val argumentIndex = argumentList.expressionList.indexOf(argument)
                    val reference = commandExpr.reference?.resolve()
                    if (reference is RsScript) {
                        val parameterList = reference.parameterList?.parameterList ?: emptyList()
                        if (argumentIndex < parameterList.size) {
                            val hookParameter = parameterList[argumentIndex]
                            val typeName = hookParameter.typeName.text
                            val type = hookParameter.typeManager.findOrNull(typeName)
                            val isHookType = type is MetaType.Hook
                            return@getCachedValue CachedValueProvider.Result(isHookType, *dependencies.toTypedArray())
                        }
                    }
                }
            }
        }
        return@getCachedValue CachedValueProvider.Result(false, *dependencies.toTypedArray())
    }

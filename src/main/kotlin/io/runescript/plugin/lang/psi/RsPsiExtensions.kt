package io.runescript.plugin.lang.psi

import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import io.runescript.plugin.lang.psi.type.RsPrimitiveType
import io.runescript.plugin.lang.psi.type.RsType

fun RsStringLiteralContent.isBasicContent(): Boolean {
    val first = node.firstChildNode ?: return true
    return first == node.lastChildNode && first.elementType == RsElementTypes.STRING_PART
}

fun RsStringLiteralContent.isHookExpression(): Boolean = CachedValuesManager.getCachedValue(this) {
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
                        val isHookType = RsPrimitiveType.lookupOrNull(hookParameter.typeName!!.text)?.isHookType()
                        return@getCachedValue CachedValueProvider.Result(isHookType ?: false, this)
                    }
                }
            }
        }
    }
    return@getCachedValue CachedValueProvider.Result(false, this)
}

fun RsType.isHookType() = when (this) {
    RsPrimitiveType.HOOK,
    RsPrimitiveType.VARPHOOK,
    RsPrimitiveType.STATHOOK,
    RsPrimitiveType.INVHOOK -> true

    else -> false
}
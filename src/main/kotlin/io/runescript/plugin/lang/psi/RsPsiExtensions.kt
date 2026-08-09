package io.runescript.plugin.lang.psi

import com.intellij.openapi.util.Key
import com.intellij.openapi.util.ModificationTracker
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import io.runescript.plugin.ide.neptune.neptuneModuleData
import io.runescript.plugin.ide.neptune.typeManager
import io.runescript.plugin.lang.psi.typechecker.type.MetaType

private val IS_HOOK_EXPRESSION_CACHE_KEY =
    Key.create<CachedValue<Boolean>>("io.runescript.plugin.isHookExpression")

fun RsStringLiteralContent.isBasicContent(): Boolean {
    val first = node.firstChildNode ?: return true
    return first == node.lastChildNode && first.elementType == RsElementTypes.STRING_PART
}

fun RsStringLiteralContent.isHookExpression(): Boolean =
    CachedValuesManager
        .getManager(project)
        .getCachedValue(
            this,
            IS_HOOK_EXPRESSION_CACHE_KEY,
            { calculateIsHookExpression() },
            false,
        )

private fun RsStringLiteralContent.calculateIsHookExpression(): CachedValueProvider.Result<Boolean> {
    val hostFile = containingFile
    val dependencies = mutableListOf<Any>(project.stubIndexModificationTracker())
    dependencies += hostFile.localModificationTracker()
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
                    dependencies += reference.containingFile.localModificationTracker()
                    val parameterList = reference.parameterList?.parameterList ?: emptyList()
                    if (argumentIndex < parameterList.size) {
                        val hookParameter = parameterList[argumentIndex]
                        val typeName = hookParameter.typeName.text
                        val type = hookParameter.typeManager.findOrNull(typeName)
                        val isHookType = type is MetaType.Hook
                        return CachedValueProvider.Result(isHookType, *dependencies.toTypedArray())
                    }
                }
            }
        }
    }
    return CachedValueProvider.Result(false, *dependencies.toTypedArray())
}

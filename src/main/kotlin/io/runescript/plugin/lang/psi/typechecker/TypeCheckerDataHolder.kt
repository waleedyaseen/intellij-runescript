package io.runescript.plugin.lang.psi.typechecker

import com.intellij.psi.PsiElement
import java.util.IdentityHashMap

internal enum class TypeCheckerAttribute {
    TRIGGER_TYPE,
    PARAMETER_TYPE,
    RETURN_TYPE,
    SCRIPT_SCOPE,
    PARAMETER_SYMBOL,
    BLOCK_SCOPE,
    SWITCH_TYPE,
    SWITCH_CASE_SCOPE,
    EXPRESSION_TYPE,
    TYPE_HINT,
    RESOLVED_SYMBOL,
    HOOK_SCOPE,
}

internal class TypeCheckerDataHolder {
    private val values = IdentityHashMap<PsiElement, Array<Any?>>()

    @Suppress("UNCHECKED_CAST")
    fun <T> get(
        element: PsiElement,
        attribute: TypeCheckerAttribute,
    ): T? = values[element]?.get(attribute.ordinal) as T?

    fun <T> set(
        element: PsiElement,
        attribute: TypeCheckerAttribute,
        value: T?,
    ) {
        if (value == null && element !in values) return
        values.computeIfAbsent(element) { arrayOfNulls(TypeCheckerAttribute.entries.size) }[attribute.ordinal] = value
    }
}

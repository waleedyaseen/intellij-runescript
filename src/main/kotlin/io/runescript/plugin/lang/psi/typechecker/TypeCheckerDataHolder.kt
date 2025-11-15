package io.runescript.plugin.lang.psi.typechecker

import com.intellij.psi.PsiElement
import java.util.*

class TypeCheckerDataHolder {

    private val map = WeakHashMap<PsiElement, MutableMap<String, Any?>>()

    @Suppress("UNCHECKED_CAST")
    fun <T> get(element: PsiElement, key: String): T? =
        map[element]?.get(key) as T?

    fun <T> set(element: PsiElement, key: String, value: T?) {
        if (value == null) {
            map[element]?.remove(key)
        } else {
            map.computeIfAbsent(element) { mutableMapOf() }[key] = value
        }
    }
}

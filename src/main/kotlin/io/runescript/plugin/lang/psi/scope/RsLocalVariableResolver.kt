package io.runescript.plugin.lang.psi.scope

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import io.runescript.plugin.lang.psi.RsLocalVariableExpression
import io.runescript.plugin.lang.psi.isForArrayDeclaration
import io.runescript.plugin.lang.psi.isForVariableDeclaration

enum class RsResolveMode {
    Variables,
    Arrays,
    Both
}

class RsLocalVariableResolver(private val name: String, private val mode: RsResolveMode) : PsiScopeProcessor {

    var declaration: RsLocalVariableExpression? = null

    override fun execute(element: PsiElement, state: ResolveState): Boolean {
        if (element is RsLocalVariableExpression) {
            if (element.name != name) return true
            val filterPassed = when (mode) {
                RsResolveMode.Variables -> element.isForVariableDeclaration()
                RsResolveMode.Arrays -> element.isForArrayDeclaration()
                RsResolveMode.Both -> element.isForVariableDeclaration() || element.isForArrayDeclaration()
            }
            if (filterPassed) {
                declaration = element
                return false
            }
        }
        return true
    }
}
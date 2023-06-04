package io.runescript.plugin.lang.psi.scope

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import io.runescript.plugin.lang.psi.RsLocalVariableExpression

class RsLocalVariableResolver(private val name: String) : PsiScopeProcessor {

    var declaration: RsLocalVariableExpression? = null

    override fun execute(element: PsiElement, state: ResolveState): Boolean {
        if (element is RsLocalVariableExpression) {
            if (element.variableName == name) {
                declaration = element
                return false
            }
        }
        return true
    }
}
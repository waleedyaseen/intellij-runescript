package io.runescript.plugin.lang.psi.scope

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import io.runescript.plugin.lang.psi.RsArrayVariableDeclarationStatement
import io.runescript.plugin.lang.psi.RsLocalVariableExpression
import io.runescript.plugin.lang.psi.RsParameter

class RsLocalVariableResolver(private val name: String, private val arraysOnly: Boolean = false) : PsiScopeProcessor {

    var declaration: RsLocalVariableExpression? = null

    override fun execute(element: PsiElement, state: ResolveState): Boolean {
        if (element is RsLocalVariableExpression) {
            if (element.name != name) return true
            if (arraysOnly) {
                val parent = element.parent
                if (parent is RsArrayVariableDeclarationStatement || (parent is RsParameter && parent.arrayTypeLiteral != null)) {
                    declaration = element
                    return false
                }
            } else {
                declaration = element
                return false
            }
        }
        return true
    }
}
package io.runescript.plugin.lang.psi.scope

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import io.runescript.plugin.lang.psi.RsLocalVariableExpression
import io.runescript.plugin.lang.psi.isForArrayDeclaration
import io.runescript.plugin.lang.psi.isForVariableDeclaration

class RsLocalVariableCollector(
    private val mode: RsResolveMode
) : PsiScopeProcessor {

    var declarations: MutableList<RsLocalVariableExpression> = arrayListOf()

    override fun execute(element: PsiElement, state: ResolveState): Boolean {
        if (element is RsLocalVariableExpression) {
            val filterPassed = when (mode) {
                RsResolveMode.Variables -> element.isForVariableDeclaration()
                RsResolveMode.Arrays -> element.isForArrayDeclaration()
                RsResolveMode.Both -> element.isForVariableDeclaration() || element.isForArrayDeclaration()
            }
            if (filterPassed) {
                declarations.add(element)
            }
        }
        return true
    }
}

fun PsiElement.collectVariableDeclarations(
    mode: RsResolveMode = RsResolveMode.Both
): List<RsLocalVariableExpression> {
    val collector = RsLocalVariableCollector(mode)
    if (!RsScopesUtil.walkUpScopes(collector, ResolveState.initial(), this)) {
        return emptyList()
    }
    return collector.declarations
}
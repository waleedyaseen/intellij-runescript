package io.runescript.plugin.lang.psi.mixin

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import io.runescript.plugin.lang.psi.RsLocalVariableDeclarationStatement

abstract class RsLocalVariableDeclarationStatementMixin(node: ASTNode) : ASTWrapperPsiElement(node), RsLocalVariableDeclarationStatement {

    override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement): Boolean {
        if (expressionList.isNotEmpty()) {
            val declaredVariable = expressionList[0]
            return processor.execute(declaredVariable, state)
        }
        return true
    }
}
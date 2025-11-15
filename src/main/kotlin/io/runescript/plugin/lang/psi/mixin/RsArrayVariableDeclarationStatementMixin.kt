package io.runescript.plugin.lang.psi.mixin

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import io.runescript.plugin.lang.psi.RsArrayVariableDeclarationStatement
import io.runescript.plugin.lang.psi.RsLocalVariableExpression

abstract class RsArrayVariableDeclarationStatementMixin(node: ASTNode) : ASTWrapperPsiElement(node), RsArrayVariableDeclarationStatement {

    override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement): Boolean {
        val declaredVariable = expressionList[0]
        return processor.execute(declaredVariable, state)
    }

    override fun getVariable(): RsLocalVariableExpression {
        return expressionList[0] as RsLocalVariableExpression
    }
}
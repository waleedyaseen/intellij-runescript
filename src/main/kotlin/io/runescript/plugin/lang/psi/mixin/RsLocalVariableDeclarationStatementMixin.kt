package io.runescript.plugin.lang.psi.mixin

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.tree.IElementType
import com.intellij.refactoring.suggested.startOffset
import io.runescript.plugin.lang.psi.RsLocalVariableDeclarationStatement
import io.runescript.plugin.lang.psi.RsLocalVariableExpression
import io.runescript.plugin.lang.psi.refs.RsLocalVariableReference
import io.runescript.plugin.lang.stubs.RsLocalVariableExpressionStub

abstract class RsLocalVariableDeclarationStatementMixin(node: ASTNode) : ASTWrapperPsiElement(node), RsLocalVariableDeclarationStatement {

    override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement): Boolean {
        val declaredVariable = expressionList[0]
        return processor.execute(declaredVariable, state)
    }
}
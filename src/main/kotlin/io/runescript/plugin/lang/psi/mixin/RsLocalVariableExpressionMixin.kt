package io.runescript.plugin.lang.psi.mixin

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import com.intellij.refactoring.suggested.startOffset
import io.runescript.plugin.lang.psi.RsLocalVariableExpression
import io.runescript.plugin.lang.psi.refs.RsLocalVariableReference

abstract class RsLocalVariableExpressionMixin(node: ASTNode) : ASTWrapperPsiElement(node), RsLocalVariableExpression {

    override fun getReference(): PsiReference {
        return RsLocalVariableReference(this)
    }

    override fun getTextOffset(): Int {
        return nameLiteral.startOffset
    }
}
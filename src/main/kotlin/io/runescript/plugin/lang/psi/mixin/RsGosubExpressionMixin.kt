package io.runescript.plugin.lang.psi.mixin

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import io.runescript.plugin.lang.psi.RsGosubExpression
import io.runescript.plugin.lang.psi.refs.RsGosubReference

abstract class RsGosubExpressionMixin(node: ASTNode) : ASTWrapperPsiElement(node), RsGosubExpression {
    override fun getReference(): PsiReference? {
        return RsGosubReference(this)
    }
}
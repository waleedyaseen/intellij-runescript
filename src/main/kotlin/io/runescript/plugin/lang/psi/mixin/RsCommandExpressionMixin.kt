package io.runescript.plugin.lang.psi.mixin

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import io.runescript.plugin.lang.psi.RsArgumentList
import io.runescript.plugin.lang.psi.RsCommandExpression
import io.runescript.plugin.lang.psi.refs.RsCommandExpressionReference

abstract class RsCommandExpressionMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node),
    RsCommandExpression {
    private val cachedReference by lazy { RsCommandExpressionReference(this) }

    override fun getReference(): PsiReference = cachedReference

    override val argumentList: RsArgumentList?
        get() = args
}

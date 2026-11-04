package io.runescript.plugin.lang.psi.mixin

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import io.runescript.plugin.lang.psi.RsJumpExpression
import io.runescript.plugin.lang.psi.refs.RsJumpReference

abstract class RsJumpExpressionMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node),
    RsJumpExpression {
    private val cachedReference by lazy { RsJumpReference(this) }

    override fun getReference(): RsJumpReference = cachedReference
}

package io.runescript.plugin.lang.psi.mixin

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import io.runescript.plugin.lang.psi.RsGosubExpression
import io.runescript.plugin.lang.psi.RsPsiImplUtil
import io.runescript.plugin.lang.psi.refs.RsGosubReference

abstract class RsGosubExpressionMixin(node: ASTNode) : ASTWrapperPsiElement(node), RsGosubExpression {

    override fun getReference(): RsGosubReference {
        return RsGosubReference(this)
    }

    override fun getNameIdentifier(): PsiElement {
        return nameLiteral
    }

    override fun getName(): String {
        return RsPsiImplUtil.getName(nameLiteral)
    }

    override fun setName(name: String): PsiElement {
        return RsPsiImplUtil.setName(nameLiteral, name)
    }
}
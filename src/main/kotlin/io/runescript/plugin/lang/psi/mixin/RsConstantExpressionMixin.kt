package io.runescript.plugin.lang.psi.mixin

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.refactoring.suggested.startOffset
import io.runescript.plugin.lang.psi.RsConstantExpression
import io.runescript.plugin.lang.psi.RsPsiImplUtil
import io.runescript.plugin.lang.psi.refs.RsConstantReference

abstract class RsConstantExpressionMixin(node: ASTNode) : ASTWrapperPsiElement(node), RsConstantExpression {

    override fun getReference(): PsiReference? {
        return RsConstantReference(this)
    }

    override fun getName(): String? {
        return RsPsiImplUtil.getName(nameLiteral)
    }

    override fun setName(name: String): PsiElement {
        return RsPsiImplUtil.setName(nameLiteral, name)
    }

    override fun getTextOffset(): Int {
        return nameLiteral.startOffset
    }

    override fun getNameIdentifier(): PsiElement? {
        return nameLiteral
    }
}
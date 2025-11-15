package io.runescript.plugin.lang.psi.mixin

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import io.runescript.plugin.lang.psi.RsDynamicExpression
import io.runescript.plugin.lang.psi.RsPsiImplUtil
import io.runescript.plugin.lang.psi.refs.RsDynamicExpressionReference
import io.runescript.plugin.lang.psi.typechecker.type.MetaType
import io.runescript.plugin.lang.psi.typechecker.typeHint

abstract class RsDynamicExpressionMixin(node: ASTNode) : ASTWrapperPsiElement(node), RsDynamicExpression {

    override fun getReference(): PsiReference? {
        if (typeHint is MetaType.Type) {
            return null
        }
        return RsDynamicExpressionReference(this)
    }

    override fun getName(): String? {
        return RsPsiImplUtil.getName(nameLiteral)
    }

    override fun setName(name: String): PsiElement {
        return RsPsiImplUtil.setName(nameLiteral, name)
    }

    override fun getNameIdentifier(): PsiElement? {
        return nameLiteral
    }
}
package io.runescript.plugin.lang.psi.mixin

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import io.runescript.plugin.lang.psi.RsHookFragment
import io.runescript.plugin.lang.psi.RsPsiImplUtil
import io.runescript.plugin.lang.psi.refs.RsHookFragmentReference

abstract class RsHookFragmentMixin(node: ASTNode) : ASTWrapperPsiElement(node), RsHookFragment {

    override fun getReference(): RsHookFragmentReference {
        return RsHookFragmentReference(this)
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
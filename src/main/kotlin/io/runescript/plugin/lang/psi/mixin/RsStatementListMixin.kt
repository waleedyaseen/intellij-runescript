package io.runescript.plugin.lang.psi.mixin

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import io.runescript.plugin.lang.psi.RsStatementList
import io.runescript.plugin.lang.psi.scope.RsScopesUtil

abstract class RsStatementListMixin(node :ASTNode) : ASTWrapperPsiElement(node), RsStatementList {

    override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement): Boolean {
        processor.handleEvent(PsiScopeProcessor.Event.SET_DECLARATION_HOLDER, this)
        if (lastParent == null) {
            return true
        }
        return RsScopesUtil.walkChildrenScopes(this, processor, state, lastParent, place)
    }
}

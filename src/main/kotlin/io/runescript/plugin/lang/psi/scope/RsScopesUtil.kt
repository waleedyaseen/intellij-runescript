package io.runescript.plugin.lang.psi.scope

import com.intellij.openapi.progress.ProgressIndicatorProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor

object RsScopesUtil {

    fun walkUpScopes(processor: PsiScopeProcessor, state: ResolveState, element: PsiElement): Boolean {
        var scope: PsiElement? = element
        var lastParent: PsiElement? = element;
        while (scope != null) {
            ProgressIndicatorProvider.checkCanceled()
            if (!scope.processDeclarations(processor, state, lastParent, element)) {
                return false
            }
            lastParent = scope
            scope = scope.context
        }
        return true
    }

    // Based on PsiScopesUtil from intellij-community repository
    fun walkChildrenScopes(thisElement: PsiElement, processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement): Boolean {
        var child: PsiElement? = null
        if (lastParent != null && lastParent.parent === thisElement) {
            child = lastParent.prevSibling
            if (child == null) {
                return true
            }
        }
        if (child == null) {
            child = thisElement.lastChild
        }
        while (child != null) {
            if (!child.processDeclarations(processor, state, null, place)) {
                return false
            }
            child = child.prevSibling
        }
        return true
    }

}
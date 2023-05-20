package io.runescript.plugin.ide.refactoring

import com.intellij.lang.refactoring.RefactoringSupportProvider
import com.intellij.psi.PsiElement
import io.runescript.plugin.lang.psi.impl.RsLocalVariableExpressionImpl

class RsRefactoringSupportProvider : RefactoringSupportProvider() {

    override fun isInplaceRenameAvailable(element: PsiElement, context: PsiElement?): Boolean {
        return element is RsLocalVariableExpressionImpl
    }
}
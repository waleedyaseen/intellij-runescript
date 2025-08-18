package io.runescript.plugin.ide.refactoring

import com.intellij.lang.refactoring.RefactoringSupportProvider
import com.intellij.psi.PsiElement
import com.intellij.refactoring.RefactoringActionHandler
import io.runescript.plugin.lang.psi.RsLocalVariableExpression

class RsRefactoringSupportProvider : RefactoringSupportProvider() {

    override fun getIntroduceVariableHandler(): RefactoringActionHandler? {
        return RsIntroduceVariableHandler()
    }

    override fun isInplaceRenameAvailable(element: PsiElement, context: PsiElement?): Boolean {
        return element is RsLocalVariableExpression
    }
}
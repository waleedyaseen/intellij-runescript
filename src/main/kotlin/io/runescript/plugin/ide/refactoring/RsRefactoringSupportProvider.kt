package io.runescript.plugin.ide.refactoring

import com.intellij.lang.refactoring.RefactoringSupportProvider
import com.intellij.psi.PsiElement
import com.intellij.refactoring.RefactoringActionHandler
import com.intellij.refactoring.changeSignature.ChangeSignatureHandler
import io.runescript.plugin.lang.psi.RsLocalVariableExpression

class RsRefactoringSupportProvider : RefactoringSupportProvider() {
    override fun getIntroduceVariableHandler(): RefactoringActionHandler? = RsIntroduceVariableHandler()

    override fun getChangeSignatureHandler(): ChangeSignatureHandler = RsChangeSignatureHandler()

    override fun isInplaceRenameAvailable(
        element: PsiElement,
        context: PsiElement?,
    ): Boolean = element is RsLocalVariableExpression
}

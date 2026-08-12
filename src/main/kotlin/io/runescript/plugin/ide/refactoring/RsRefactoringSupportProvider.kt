package io.runescript.plugin.ide.refactoring

import com.intellij.lang.refactoring.RefactoringSupportProvider
import com.intellij.psi.PsiElement
import com.intellij.refactoring.RefactoringActionHandler
import com.intellij.refactoring.changeSignature.ChangeSignatureHandler
import io.runescript.plugin.lang.psi.RsLocalVariableDeclarationStatement
import io.runescript.plugin.lang.psi.RsLocalVariableExpression
import io.runescript.plugin.lang.psi.RsParameter
import io.runescript.plugin.lang.psi.RsScript

class RsRefactoringSupportProvider : RefactoringSupportProvider() {
    override fun getIntroduceVariableHandler(): RefactoringActionHandler? = RsIntroduceVariableHandler()

    override fun getChangeSignatureHandler(): ChangeSignatureHandler = RsChangeSignatureHandler()

    override fun isSafeDeleteAvailable(element: PsiElement): Boolean =
        element is RsScript ||
            element is RsParameter ||
            (
                element is RsLocalVariableExpression &&
                    (element.parent is RsParameter || element.parent is RsLocalVariableDeclarationStatement)
            )

    override fun isInplaceRenameAvailable(
        element: PsiElement,
        context: PsiElement?,
    ): Boolean = element is RsLocalVariableExpression || element is RsScript
}

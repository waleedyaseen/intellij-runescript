package io.runescript.plugin.ide.usages

import com.intellij.codeInsight.highlighting.ReadWriteAccessDetector
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import io.runescript.plugin.lang.psi.RsAssignmentStatement
import io.runescript.plugin.lang.psi.RsDynamicExpression
import io.runescript.plugin.lang.psi.RsLocalVariableDeclarationStatement
import io.runescript.plugin.lang.psi.RsLocalVariableExpression

class RsReadWriteAccessDetector : ReadWriteAccessDetector() {

    // TODO(Walied): Add array variable support and do not assume declaration
    //  without initializer are write accesses.

    override fun isReadWriteAccessible(element: PsiElement): Boolean {
        return element is RsLocalVariableExpression
    }

    override fun isDeclarationWriteAccess(element: PsiElement): Boolean {
        require(element is RsLocalVariableExpression)
        val parent = element.parent
        return element is RsAssignmentStatement || parent is RsLocalVariableDeclarationStatement
    }

    override fun getReferenceAccess(referencedElement: PsiElement, reference: PsiReference): Access {
        return getExpressionAccess(reference.element)
    }

    override fun getExpressionAccess(expression: PsiElement): Access {
        require(expression is RsLocalVariableExpression || expression is RsDynamicExpression)
        val parent = expression.parent
        if (parent is RsAssignmentStatement || parent is RsLocalVariableDeclarationStatement) {
            return Access.Write
        }
        return Access.Read
    }

}
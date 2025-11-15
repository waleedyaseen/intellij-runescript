package io.runescript.plugin.ide.usages

import com.intellij.codeInsight.highlighting.ReadWriteAccessDetector
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import io.runescript.plugin.lang.doc.psi.impl.RsDocName
import io.runescript.plugin.lang.psi.*
import io.runescript.plugin.symbollang.psi.RsSymSymbol
import io.runescript.plugin.symbollang.psi.isVarFile

class RsReadWriteAccessDetector : ReadWriteAccessDetector() {

    // TODO(Walied): Add array variable support and do not assume declaration
    //  without initializer are write accesses.

    override fun isReadWriteAccessible(element: PsiElement): Boolean {
        if (element is RsSymSymbol) {
            return element.containingFile?.isVarFile() ?: false
        }
        return element is RsLocalVariableExpression
    }

    override fun isDeclarationWriteAccess(element: PsiElement): Boolean {
        require(element is RsLocalVariableExpression || element is RsSymSymbol)
        val parent = element.parent
        return element is RsAssignmentStatement || parent is RsLocalVariableDeclarationStatement
                || parent is RsPrefixExpression || parent is RsPostfixExpression
    }

    override fun getReferenceAccess(referencedElement: PsiElement, reference: PsiReference): Access {
        return getExpressionAccess(reference.element)
    }

    override fun getExpressionAccess(expression: PsiElement): Access {
        require(expression is RsLocalVariableExpression
                || expression is RsScopedVariableExpression
                || expression is RsDynamicExpression
                || expression is RsDocName)
        val parent = expression.parent
        if (parent is RsAssignmentStatement || parent is RsLocalVariableDeclarationStatement
            || parent is RsPrefixExpression || parent is RsPostfixExpression) {
            return Access.Write
        }
        return Access.Read
    }

}
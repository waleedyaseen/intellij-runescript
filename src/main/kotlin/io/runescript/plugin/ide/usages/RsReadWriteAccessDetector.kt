package io.runescript.plugin.ide.usages

import com.intellij.codeInsight.highlighting.ReadWriteAccessDetector
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.util.parentOfType
import io.runescript.plugin.lang.doc.psi.impl.RsDocName
import io.runescript.plugin.lang.psi.RsArrayAccessExpression
import io.runescript.plugin.lang.psi.RsArrayVariableDeclarationStatement
import io.runescript.plugin.lang.psi.RsAssignmentStatement
import io.runescript.plugin.lang.psi.RsDynamicExpression
import io.runescript.plugin.lang.psi.RsLocalVariableDeclarationStatement
import io.runescript.plugin.lang.psi.RsLocalVariableExpression
import io.runescript.plugin.lang.psi.RsParameter
import io.runescript.plugin.lang.psi.RsPostfixExpression
import io.runescript.plugin.lang.psi.RsPrefixExpression
import io.runescript.plugin.lang.psi.RsScopedVariableExpression
import io.runescript.plugin.symbollang.psi.RsSymSymbol
import io.runescript.plugin.symbollang.psi.isVarFile

class RsReadWriteAccessDetector : ReadWriteAccessDetector() {
    override fun isReadWriteAccessible(element: PsiElement): Boolean {
        if (element is RsSymSymbol) {
            return element.containingFile?.isVarFile() ?: false
        }
        return element is RsLocalVariableExpression
    }

    override fun isDeclarationWriteAccess(element: PsiElement): Boolean {
        require(element is RsLocalVariableExpression || element is RsSymSymbol)
        return when (val parent = element.parent) {
            is RsLocalVariableDeclarationStatement -> parent.initializer != null

            is RsArrayVariableDeclarationStatement,
            is RsParameter,
            -> true

            else -> false
        }
    }

    override fun getReferenceAccess(
        referencedElement: PsiElement,
        reference: PsiReference,
    ): Access = getExpressionAccess(reference.element)

    override fun getExpressionAccess(expression: PsiElement): Access {
        require(
            expression is RsLocalVariableExpression ||
                expression is RsScopedVariableExpression ||
                expression is RsDynamicExpression ||
                expression is RsDocName,
        )
        val accessTarget = expression.accessTarget()
        val assignment = accessTarget.parentOfType<RsAssignmentStatement>(withSelf = false)
        if (assignment != null &&
            accessTarget in assignment.expressionList.takeWhile { it.textRange.startOffset < assignment.equal.textRange.startOffset }
        ) {
            return Access.Write
        }
        val parent = accessTarget.parent
        if ((parent is RsPrefixExpression && parent.expression === accessTarget) ||
            (parent is RsPostfixExpression && parent.expression === accessTarget)
        ) {
            return Access.ReadWrite
        }
        return Access.Read
    }

    private fun PsiElement.accessTarget(): PsiElement {
        val arrayAccess = parent as? RsArrayAccessExpression ?: return this
        return if (arrayAccess.expressionList.firstOrNull() === this) arrayAccess else this
    }
}

package io.runescript.plugin.ide.usages

import com.intellij.codeInsight.highlighting.ReadWriteAccessDetector
import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.lang.cacheBuilder.WordsScanner
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.tree.TokenSet
import io.runescript.plugin.lang.lexer.RsLexerAdapter
import io.runescript.plugin.lang.lexer.RsLexerInfo
import io.runescript.plugin.lang.psi.RsArrayVariableAssignmentStatement
import io.runescript.plugin.lang.psi.RsElementTypes
import io.runescript.plugin.lang.psi.RsLocalVariableAssignmentStatement
import io.runescript.plugin.lang.psi.RsLocalVariableDeclarationStatement
import io.runescript.plugin.lang.psi.RsLocalVariableExpression
import io.runescript.plugin.lang.psi.RsTokenTypesSets

class RsReadWriteAccessDetector : ReadWriteAccessDetector() {

    override fun isReadWriteAccessible(element: PsiElement): Boolean {
        return element is RsLocalVariableExpression
    }

    override fun isDeclarationWriteAccess(element: PsiElement): Boolean {
        require(element is RsLocalVariableExpression)
        val parent = element.parent
        if (element is RsLocalVariableAssignmentStatement || parent is RsLocalVariableDeclarationStatement) {
            return true
        }
        return false
    }

    override fun getReferenceAccess(referencedElement: PsiElement, reference: PsiReference): Access {
        return getExpressionAccess(reference.element)
    }

    override fun getExpressionAccess(expression: PsiElement): Access {
        require(expression is RsLocalVariableExpression)
        val parent = expression.parent
        if (parent is RsLocalVariableAssignmentStatement || parent is RsLocalVariableDeclarationStatement) {
            return Access.Write
        }
        return Access.Read
    }

}
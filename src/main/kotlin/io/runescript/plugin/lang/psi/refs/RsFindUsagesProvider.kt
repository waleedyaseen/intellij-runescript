package io.runescript.plugin.lang.psi.refs

import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.lang.cacheBuilder.WordsScanner
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import io.runescript.plugin.lang.lexer.RsLexerAdapter
import io.runescript.plugin.lang.lexer.RsLexerInfo
import io.runescript.plugin.lang.psi.RsLocalVariableExpression
import io.runescript.plugin.lang.psi.RsTokenTypesSets
import io.runescript.plugin.lang.psi.RsElementTypes
import io.runescript.plugin.lang.psi.RsLocalVariableDeclarationStatement

class RsFindUsagesProvider : FindUsagesProvider {

    override fun getWordsScanner(): WordsScanner {
        return DefaultWordsScanner(
            RsLexerAdapter(RsLexerInfo(emptyList())),
            TokenSet.create(RsElementTypes.IDENTIFIER),
            RsTokenTypesSets.COMMENTS,
            TokenSet.create(RsElementTypes.TYPE_LITERAL, RsElementTypes.ARRAY_TYPE_LITERAL),
        )
    }

    override fun canFindUsagesFor(psiElement: PsiElement): Boolean {
        return psiElement is RsLocalVariableExpression && psiElement.parent is RsLocalVariableDeclarationStatement
    }

    override fun getHelpId(psiElement: PsiElement): String? {
        return null
    }

    override fun getType(element: PsiElement): String {
        if (element is RsLocalVariableExpression) {
            return "Local Variable"
        }
        return ""
    }

    override fun getDescriptiveName(element: PsiElement): String {
        if (element is RsLocalVariableExpression) {
            return element.nameLiteral.text
        }
        return ""
    }

    override fun getNodeText(element: PsiElement, useFullName: Boolean): String {
        if (element is RsLocalVariableExpression) {
            return "GRGRGRGR ${element.nameLiteral.text}"
        }
        return ""
    }
}
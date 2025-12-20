package io.runescript.plugin.ide.usages

import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.lang.cacheBuilder.WordsScanner
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import io.runescript.plugin.ide.neptune.DEFAULT_RESOLVED_DATA
import io.runescript.plugin.lang.lexer.RsLexerAdapter
import io.runescript.plugin.lang.lexer.RsLexerInfo
import io.runescript.plugin.lang.psi.RsElementTypes
import io.runescript.plugin.lang.psi.RsLocalVariableExpression
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.RsTokenTypesSets
import io.runescript.plugin.lang.psi.scriptName
import io.runescript.plugin.symbollang.psi.RsSymSymbol

class RsFindUsagesProvider : FindUsagesProvider {

    override fun getWordsScanner(): WordsScanner {
        return DefaultWordsScanner(
                RsLexerAdapter(RsLexerInfo(DEFAULT_RESOLVED_DATA.types)),
                TokenSet.create(RsElementTypes.IDENTIFIER),
                RsTokenTypesSets.COMMENTS,
                TokenSet.create(RsElementTypes.TYPE_LITERAL),
        )
    }

    override fun canFindUsagesFor(psiElement: PsiElement): Boolean {
        return psiElement is RsLocalVariableExpression || psiElement is RsScript || psiElement is RsSymSymbol
    }

    override fun getHelpId(psiElement: PsiElement): String? {
        return null
    }

    override fun getType(element: PsiElement): String {
        if (element is RsLocalVariableExpression) {
            return "Local variable"
        }
        if (element is RsScript) {
            return "Script"
        }
        if (element is RsSymSymbol) {
            return "Symbol"
        }
        return ""
    }

    override fun getDescriptiveName(element: PsiElement): String {
        if (element is RsLocalVariableExpression) {
            return element.nameLiteral.text
        }
        if (element is RsScript) {
            return element.scriptName
        }
        if (element is RsSymSymbol) {
            return element.name!!
        }
        return ""
    }

    override fun getNodeText(element: PsiElement, useFullName: Boolean): String {
        if (element is RsLocalVariableExpression) {
            return element.nameLiteral.text
        }
        if (element is RsScript) {
            return element.scriptName
        }
        if (element is RsSymSymbol) {
            return element.name!!
        }
        return ""
    }
}
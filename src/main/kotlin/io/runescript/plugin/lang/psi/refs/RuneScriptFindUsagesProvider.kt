package io.runescript.plugin.lang.psi.refs

import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.lang.cacheBuilder.WordsScanner
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import io.runescript.plugin.lang.lexer.RuneScriptLexerAdapter
import io.runescript.plugin.lang.lexer.RuneScriptLexerInfo
import io.runescript.plugin.lang.psi.RuneScriptLocalVariableExpression
import io.runescript.plugin.lang.psi.RuneScriptTokenTypesSets
import io.runescript.plugin.lang.psi.RuneScriptTypes

class RuneScriptFindUsagesProvider : FindUsagesProvider {

    override fun getWordsScanner(): WordsScanner? {
        return DefaultWordsScanner(
            RuneScriptLexerAdapter(RuneScriptLexerInfo(emptyList())),
            TokenSet.create(RuneScriptTypes.IDENTIFIER),
            RuneScriptTokenTypesSets.COMMENTS,
            TokenSet.create(RuneScriptTypes.TYPE_NAME, RuneScriptTypes.ARRAY_TYPE_NAME),
        )
    }

    override fun canFindUsagesFor(psiElement: PsiElement): Boolean {
        return false
    }

    override fun getHelpId(psiElement: PsiElement): String? {
        return null
    }

    override fun getType(element: PsiElement): String {
        if (element is RuneScriptLocalVariableExpression) {
            return "Local Variable"
        }
        return ""
    }

    override fun getDescriptiveName(element: PsiElement): String {
        if (element is RuneScriptLocalVariableExpression) {
            return element.nameLiteral.text
        }
        return ""
    }

    override fun getNodeText(element: PsiElement, useFullName: Boolean): String {
        if (element is RuneScriptLocalVariableExpression) {
            return "GRGRGRGR ${element.nameLiteral.text}"
        }
        return ""
    }
}
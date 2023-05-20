package io.runescript.plugin.lang.psi.refs

import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.lang.cacheBuilder.WordsScanner
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.lexer.Lexer
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.childrenOfType
import com.intellij.psi.util.findParentOfType
import io.runescript.plugin.lang.lexer.RuneScriptLexerAdapter
import io.runescript.plugin.lang.lexer.RuneScriptLexerInfo
import io.runescript.plugin.lang.psi.*
import io.runescript.plugin.lang.psi.named.RuneScriptNamedElement

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
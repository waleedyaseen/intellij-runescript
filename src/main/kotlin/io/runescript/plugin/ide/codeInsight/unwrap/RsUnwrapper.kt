package io.runescript.plugin.ide.codeInsight.unwrap

import com.intellij.codeInsight.unwrap.AbstractUnwrapper
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import io.runescript.plugin.lang.psi.RsBlockStatement
import io.runescript.plugin.lang.psi.RsEmptyStatement
import io.runescript.plugin.lang.psi.RsStatement

abstract class RsUnwrapper(
    description: String,
) : AbstractUnwrapper<RsUnwrapper.Context>(description) {
    override fun createContext(): Context = Context()

    class Context : AbstractContext() {
        fun extractFromBlockOrSingleStatement(
            statement: RsStatement?,
            target: PsiElement,
        ) {
            if (statement is RsBlockStatement) {
                val body = statement.statementList
                extract(body.firstChild, body.lastChild, target)
                return
            }
            if (statement != null && statement !is RsEmptyStatement) {
                extract(statement, statement, target)
            }
        }

        override fun isWhiteSpace(element: PsiElement): Boolean = element is PsiWhiteSpace

        fun deleteRange(
            first: PsiElement,
            last: PsiElement,
        ) {
            if (myIsEffective) {
                first.parent.deleteChildRange(first, last)
            }
        }
    }
}

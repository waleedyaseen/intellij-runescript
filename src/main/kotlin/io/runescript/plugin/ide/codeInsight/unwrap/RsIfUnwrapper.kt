package io.runescript.plugin.ide.codeInsight.unwrap

import com.intellij.psi.PsiElement
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.lang.psi.RsIfStatement

class RsIfUnwrapper : RsUnwrapper(RsBundle.message("unwrap.if.description")) {
    override fun isApplicableTo(element: PsiElement): Boolean = element is RsIfStatement

    override fun doUnwrap(
        element: PsiElement,
        context: Context,
    ) {
        val statement = element as RsIfStatement
        context.extractFromBlockOrSingleStatement(statement.trueStatement, statement)
        context.delete(statement)
    }
}

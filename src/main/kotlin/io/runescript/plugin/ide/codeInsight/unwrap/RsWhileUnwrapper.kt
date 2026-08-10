package io.runescript.plugin.ide.codeInsight.unwrap

import com.intellij.psi.PsiElement
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.lang.psi.RsWhileStatement

class RsWhileUnwrapper : RsUnwrapper(RsBundle.message("unwrap.while.description")) {
    override fun isApplicableTo(element: PsiElement): Boolean = element is RsWhileStatement

    override fun doUnwrap(
        element: PsiElement,
        context: Context,
    ) {
        val statement = element as RsWhileStatement
        context.extractFromBlockOrSingleStatement(statement.statement, statement)
        context.delete(statement)
    }
}

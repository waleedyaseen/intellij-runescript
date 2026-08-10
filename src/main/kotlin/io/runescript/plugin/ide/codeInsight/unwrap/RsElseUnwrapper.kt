package io.runescript.plugin.ide.codeInsight.unwrap

import com.intellij.psi.PsiElement
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.lang.psi.RsIfStatement

class RsElseUnwrapper : RsUnwrapper(RsBundle.message("unwrap.else.description")) {
    override fun isApplicableTo(element: PsiElement): Boolean = element is RsIfStatement && element.falseStatement != null

    override fun doUnwrap(
        element: PsiElement,
        context: Context,
    ) {
        val statement = element as RsIfStatement
        context.extractFromBlockOrSingleStatement(statement.falseStatement, statement)
        context.delete(statement)
    }
}

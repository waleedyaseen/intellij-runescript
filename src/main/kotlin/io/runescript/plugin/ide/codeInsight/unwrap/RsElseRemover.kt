package io.runescript.plugin.ide.codeInsight.unwrap

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.lang.psi.RsIfStatement

class RsElseRemover : RsUnwrapper(RsBundle.message("unwrap.remove.else.description")) {
    override fun isApplicableTo(element: PsiElement): Boolean = element is RsIfStatement && element.falseStatement != null

    override fun doUnwrap(
        element: PsiElement,
        context: Context,
    ) {
        val statement = element as RsIfStatement
        val elseKeyword = statement.getElse() ?: return
        val elseBranch = statement.falseStatement ?: return
        var first: PsiElement = elseKeyword
        while (first.prevSibling is PsiWhiteSpace) {
            first = first.prevSibling
        }
        val last = elseBranch.nextSibling.takeIf { it is PsiWhiteSpace } ?: elseBranch
        context.deleteRange(first, last)
    }
}

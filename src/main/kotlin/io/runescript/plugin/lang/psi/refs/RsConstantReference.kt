package io.runescript.plugin.lang.psi.refs

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.ResolveResult
import io.runescript.plugin.lang.psi.RsConstantExpression
import io.runescript.plugin.symbollang.psi.index.RsSymbolIndex

class RsConstantReference(
    element: RsConstantExpression,
) : RsCachedPolyVariantReference<RsConstantExpression>(element, { it.nameLiteral.textRangeInParent }) {
    override fun resolveInner(incompleteCode: Boolean): Array<ResolveResult> {
        val symbol =
            RsSymbolIndex.lookup(element, "constant", element.name!!)
                ?: return emptyArray()
        return arrayOf(PsiElementResolveResult(symbol))
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        element.setName(newElementName)
        return element
    }
}

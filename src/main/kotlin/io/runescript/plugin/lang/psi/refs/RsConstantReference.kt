package io.runescript.plugin.lang.psi.refs

import com.intellij.psi.*
import io.runescript.plugin.lang.psi.RsConstantExpression
import io.runescript.plugin.symbollang.psi.index.RsSymbolIndex

class RsConstantReference(element: RsConstantExpression) :
    PsiReferenceBase<RsConstantExpression>(element, element.nameLiteral.textRangeInParent), PsiPolyVariantReference {

    override fun resolve(): PsiElement? {
        val result = multiResolve(false)
        return result.singleOrNull()?.element
    }

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val symbol = RsSymbolIndex.lookup(element, "constant", element.name!!)
            ?: return emptyArray()
        return arrayOf(PsiElementResolveResult(symbol))
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        element.setName(newElementName)
        return element
    }
}
package io.runescript.plugin.lang.psi.refs

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.ResolveResult
import io.runescript.plugin.lang.psi.RsStringLiteralExpression
import io.runescript.plugin.lang.psi.isBasicContent
import io.runescript.plugin.lang.psi.typechecker.type.Type
import io.runescript.plugin.lang.psi.typechecker.typeCheckedType
import io.runescript.plugin.symbollang.psi.index.RsSymbolIndex

class RsStringLiteralReference(
    element: RsStringLiteralExpression,
) : RsCachedPolyVariantReference<RsStringLiteralExpression>(element, { it.stringLiteralContent.textRangeInParent }) {
    override fun resolveInner(incompleteCode: Boolean): Array<ResolveResult> = resolveElement(element, element.typeCheckedType)

    override fun getVariants(): Array<out LookupElement> = LookupElement.EMPTY_ARRAY

    override fun handleElementRename(newElementName: String): PsiElement = element.setName(newElementName)

    companion object {
        fun resolveElement(
            element: RsStringLiteralExpression,
            type: Type,
        ): Array<ResolveResult> {
            if (!element.stringLiteralContent.isBasicContent()) {
                return emptyArray()
            }
            val elementName = element.stringLiteralContent.text

            val resolvedConfig = RsSymbolIndex.lookup(element, type, elementName)
            if (resolvedConfig != null) {
                return arrayOf(PsiElementResolveResult(resolvedConfig))
            }
            return emptyArray()
        }
    }
}

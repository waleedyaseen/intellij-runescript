package io.runescript.plugin.lang.psi.refs

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiPolyVariantReferenceBase
import com.intellij.psi.ResolveResult
import io.runescript.plugin.lang.psi.RsStringLiteralExpression
import io.runescript.plugin.lang.psi.isBasicContent
import io.runescript.plugin.lang.psi.type.RsPrimitiveType
import io.runescript.plugin.lang.psi.type.RsType
import io.runescript.plugin.lang.psi.type.type
import io.runescript.plugin.symbollang.psi.index.RsSymbolIndex

class RsStringLiteralReference(element: RsStringLiteralExpression) :
    PsiPolyVariantReferenceBase<RsStringLiteralExpression>(element, element.stringLiteralContent.textRangeInParent) {

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        return resolveElement(element, element.type)
    }

    override fun getVariants(): Array<out LookupElement> = LookupElement.EMPTY_ARRAY

    override fun handleElementRename(newElementName: String): PsiElement {
        return element.setName(newElementName)
    }

    companion object {
        fun resolveElement(element: RsStringLiteralExpression, type: RsType): Array<ResolveResult> {
            if (!element.stringLiteralContent.isBasicContent()) {
                return emptyArray()
            }
            val elementName = element.stringLiteralContent.text

            if (type is RsPrimitiveType) {
                val resolvedConfig = RsSymbolIndex.lookup(element, type, elementName)
                if (resolvedConfig != null) {
                    return arrayOf(PsiElementResolveResult(resolvedConfig))
                }
            }
            return emptyArray()
        }
    }
}
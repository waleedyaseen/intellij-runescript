package io.runescript.plugin.lang.psi.refs

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiPolyVariantReferenceBase
import com.intellij.psi.ResolveResult
import io.runescript.plugin.lang.psi.RsIntegerLiteralExpression
import io.runescript.plugin.lang.psi.typechecker.type.Type
import io.runescript.plugin.lang.psi.typechecker.typeCheckedType
import io.runescript.plugin.symbollang.psi.index.RsSymbolIndex

class RsIntegerLiteralReference(element: RsIntegerLiteralExpression) :
    PsiPolyVariantReferenceBase<RsIntegerLiteralExpression>(element, TextRange(0, element.textLength)) {

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        return resolveElement(element, element.typeCheckedType)
    }

    override fun getVariants(): Array<out LookupElement> = LookupElement.EMPTY_ARRAY

    override fun handleElementRename(newElementName: String): PsiElement {
        return element.setName(newElementName)
    }

    companion object {
        fun resolveElement(element: RsIntegerLiteralExpression, type: Type): Array<ResolveResult> {
            val elementName = element.text

            val resolvedConfig = RsSymbolIndex.lookup(element, type, elementName)
            if (resolvedConfig != null) {
                return arrayOf(PsiElementResolveResult(resolvedConfig))
            }
            return emptyArray()
        }
    }
}
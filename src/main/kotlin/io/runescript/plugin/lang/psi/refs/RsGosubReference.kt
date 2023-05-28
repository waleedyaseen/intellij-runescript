package io.runescript.plugin.lang.psi.refs

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.openapi.diagnostic.RuntimeExceptionWithAttachments
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiPolyVariantReferenceBase
import com.intellij.psi.ResolveResult
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import io.runescript.plugin.lang.psi.RsGosubExpression
import io.runescript.plugin.lang.psi.RsScriptHeader
import io.runescript.plugin.lang.stubs.index.RsGotoScriptIndex

class RsGosubReference(element: RsGosubExpression) : PsiPolyVariantReferenceBase<RsGosubExpression>(element, element.nameLiteral.textRangeInParent) {

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val elements = StubIndex.getElements(RsGotoScriptIndex.KEY, element.nameLiteral.text, element.project, GlobalSearchScope.allScope(element.project), RsScriptHeader::class.java)
                .filter { it.scriptName.triggerExpression.text == "proc" }
        return elements.map { PsiElementResolveResult(it) }.toTypedArray()
    }

    override fun getVariants(): Array<out LookupElement> = LookupElement.EMPTY_ARRAY

    override fun handleElementRename(newElementName: String): PsiElement {
        return element
    }
}
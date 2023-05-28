package io.runescript.plugin.lang.psi.refs

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiPolyVariantReferenceBase
import com.intellij.psi.ResolveResult
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import io.runescript.plugin.lang.psi.RsCommandExpression
import io.runescript.plugin.lang.psi.op.RsOpCommand
import io.runescript.plugin.lang.stubs.index.RsCommandIndex

class RsCommandExpressionReference(element: RsCommandExpression) : PsiPolyVariantReferenceBase<RsCommandExpression>(element, element.nameLiteral.textRangeInParent) {

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val elements = StubIndex.getElements(RsCommandIndex.KEY, element.nameLiteral.text, element.project, GlobalSearchScope.allScope(element.project), RsOpCommand::class.java)
        return elements.map { PsiElementResolveResult(it) }.toTypedArray()
    }

    override fun getVariants(): Array<out LookupElement> = LookupElement.EMPTY_ARRAY

    override fun handleElementRename(newElementName: String): PsiElement {
        return element
    }
}
package io.runescript.plugin.lang.psi.refs

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.openapi.module.ModuleUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiPolyVariantReferenceBase
import com.intellij.psi.ResolveResult
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import io.runescript.plugin.lang.psi.RsHookFragment
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.stubs.index.RsClientScriptIndex

class RsHookFragmentReference(element: RsHookFragment) : PsiPolyVariantReferenceBase<RsHookFragment>(element, element.nameLiteral.textRangeInParent) {

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val module = ModuleUtil.findModuleForPsiElement(element) ?: return emptyArray()
        val elements = StubIndex.getElements(RsClientScriptIndex.KEY, element.nameLiteral.text, element.project, GlobalSearchScope.moduleScope(module), RsScript::class.java)
        return elements.map { PsiElementResolveResult(it) }.toTypedArray()
    }

    override fun getVariants(): Array<out LookupElement> = LookupElement.EMPTY_ARRAY

    override fun handleElementRename(newElementName: String): PsiElement {
        return element.setName(newElementName)
    }
}
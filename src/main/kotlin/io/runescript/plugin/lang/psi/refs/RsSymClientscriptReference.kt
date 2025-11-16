package io.runescript.plugin.lang.psi.refs

import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiPolyVariantReferenceBase
import com.intellij.psi.ResolveResult
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.stubs.index.RsScriptIndex
import io.runescript.plugin.symbollang.psi.RsSymField
import io.runescript.plugin.symbollang.psi.RsSymSymbol

class RsSymClientscriptReference(element: RsSymSymbol) : PsiPolyVariantReferenceBase<RsSymSymbol>(
    element,
    extractRangeInParent(element.fieldList[1]),
) {
    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult?> {
        val text = element.fieldList.getOrNull(1)?.text ?: return emptyArray()
        val module = ModuleUtil.findModuleForPsiElement(element) ?: return emptyArray()
        val searchScope = GlobalSearchScope.moduleScope(module)
        return StubIndex.getElements(
            RsScriptIndex.KEY,
            text,
            element.project,
            searchScope,
            RsScript::class.java
        ).map { PsiElementResolveResult(it) }.toTypedArray()
    }

    companion object {
        private fun extractRangeInParent(ele: RsSymField): TextRange {
            val baseRange = ele.textRangeInParent
            val commaIndex = ele.text.indexOf(',')
            return TextRange(baseRange.startOffset + commaIndex + 1, baseRange.endOffset - 1)
        }
    }
}


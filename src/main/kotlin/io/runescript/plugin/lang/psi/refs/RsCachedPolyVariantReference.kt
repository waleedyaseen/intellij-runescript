package io.runescript.plugin.lang.psi.refs

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReferenceBase
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache

abstract class RsCachedPolyVariantReference<T : PsiElement>(
    element: T,
    private val rangeProvider: (T) -> TextRange,
) : PsiPolyVariantReferenceBase<T>(element, rangeProvider(element)) {
    final override fun getRangeInElement(): TextRange = rangeProvider(element)

    final override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> =
        ResolveCache
            .getInstance(element.project)
            .resolveWithCaching(this, RESOLVER, true, incompleteCode)

    protected abstract fun resolveInner(incompleteCode: Boolean): Array<ResolveResult>

    private companion object {
        val RESOLVER =
            ResolveCache.PolyVariantResolver<RsCachedPolyVariantReference<*>> { reference, incompleteCode ->
                reference.resolveInner(incompleteCode)
            }
    }
}

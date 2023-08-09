package io.runescript.plugin.lang.psi.refs

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import io.runescript.plugin.lang.psi.RsDynamicExpression
import io.runescript.plugin.lang.psi.scope.RsLocalVariableResolver
import io.runescript.plugin.lang.psi.scope.RsResolveMode
import io.runescript.plugin.lang.psi.scope.RsScopesUtil
import io.runescript.plugin.lang.psi.type.RsPrimitiveType
import io.runescript.plugin.lang.psi.type.RsType
import io.runescript.plugin.lang.psi.type.type
import io.runescript.plugin.oplang.psi.RsOpCommand
import io.runescript.plugin.oplang.psi.index.RsCommandIndex
import io.runescript.plugin.symbollang.psi.index.RsSymbolIndex

class RsDynamicExpressionReference(element: RsDynamicExpression) : PsiPolyVariantReferenceBase<RsDynamicExpression>(element, element.nameLiteral.textRangeInParent) {

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        return resolveElement(element, element.type)
    }

    override fun getVariants(): Array<out LookupElement> = LookupElement.EMPTY_ARRAY

    override fun handleElementRename(newElementName: String): PsiElement {
        return element.setName(newElementName)
    }

    companion object {
        fun resolveElement(element: RsDynamicExpression, type: RsType): Array<ResolveResult> {
            val elementName = element.nameLiteral.text

            // Try to resolve the element as a local array reference.
            val localArrayResolver = RsLocalVariableResolver(elementName, RsResolveMode.Arrays)
            RsScopesUtil.walkUpScopes(localArrayResolver, ResolveState.initial(), element)
            val localArray = localArrayResolver.declaration
            if (localArray != null) {
                return arrayOf(PsiElementResolveResult(localArray))
            }

            // Try to resolve the element as a config reference.
            val project = element.project
            if (type is RsPrimitiveType) {
                val resolvedConfig = RsSymbolIndex.lookup(project, type, elementName)
                if (resolvedConfig != null) {
                    return arrayOf(PsiElementResolveResult(resolvedConfig))
                }
            }

            // Try to resolve the element as a command reference.
            val searchScope = GlobalSearchScope.allScope(project)
            return StubIndex.getElements(RsCommandIndex.KEY, elementName, project, searchScope, RsOpCommand::class.java)
                    .map { PsiElementResolveResult(it) }
                    .toTypedArray()
        }
    }
}
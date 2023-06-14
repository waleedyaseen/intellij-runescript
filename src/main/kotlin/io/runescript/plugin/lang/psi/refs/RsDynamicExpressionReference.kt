package io.runescript.plugin.lang.psi.refs

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import io.runescript.plugin.lang.psi.RsDynamicExpression
import io.runescript.plugin.lang.psi.RsLocalVariableExpression
import io.runescript.plugin.lang.psi.op.RsOpCommand
import io.runescript.plugin.lang.psi.scope.RsLocalVariableResolver
import io.runescript.plugin.lang.psi.scope.RsScopesUtil
import io.runescript.plugin.lang.stubs.index.RsCommandIndex
import io.runescript.plugin.symbollang.psi.RsSymSymbol
import io.runescript.plugin.symbollang.psi.index.RsSymbolIndex

class RsDynamicExpressionReference(element: RsDynamicExpression) : PsiPolyVariantReferenceBase<RsDynamicExpression>(element, element.nameLiteral.textRangeInParent) {

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        resolveArray()?.let { return toResolveResult(listOf(it)) }
        val commands = StubIndex.getElements(RsCommandIndex.KEY, element.nameLiteral.text, element.project, GlobalSearchScope.allScope(element.project), RsOpCommand::class.java)
        if (commands.isNotEmpty()) {
            return toResolveResult(commands)
        }
        val configs = StubIndex.getElements(RsSymbolIndex.KEY, element.nameLiteral.text, element.project, GlobalSearchScope.allScope(element.project), RsSymSymbol::class.java)
        if (configs.isNotEmpty()) {
            return toResolveResult(configs)
        }
        return emptyArray()
    }

    private fun toResolveResult(elements: Iterable<PsiElement>): Array<ResolveResult> {
        return elements.map { PsiElementResolveResult(it) }.toTypedArray()
    }

    private fun resolveArray(): RsLocalVariableExpression? {
        val resolver = RsLocalVariableResolver(element.nameLiteral.text, arraysOnly = true)
        RsScopesUtil.walkUpScopes(resolver, ResolveState.initial(), element)
        return resolver.declaration
    }

    override fun getVariants(): Array<out LookupElement> = LookupElement.EMPTY_ARRAY

    override fun handleElementRename(newElementName: String): PsiElement {
        return element.setName(newElementName)
    }
}
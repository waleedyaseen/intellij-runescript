package io.runescript.plugin.lang.psi.refs

import com.intellij.psi.*
import io.runescript.plugin.lang.psi.RsLocalVariableExpression
import io.runescript.plugin.lang.psi.isForArrayAccess
import io.runescript.plugin.lang.psi.scope.RsLocalVariableResolver
import io.runescript.plugin.lang.psi.scope.RsResolveMode
import io.runescript.plugin.lang.psi.scope.RsScopesUtil

class RsLocalVariableReference(element: RsLocalVariableExpression) :
        PsiReferenceBase<RsLocalVariableExpression>(element, element.nameLiteral.textRangeInParent), PsiPolyVariantReference {

    override fun resolve(): PsiElement? {
        val result = multiResolve(false);
        return if (result.isEmpty()) null else result[0].element
    }

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val resolveMode = if (element.isForArrayAccess()) RsResolveMode.Arrays else RsResolveMode.Variables
        val resolver = RsLocalVariableResolver(element.name!!, resolveMode)
        RsScopesUtil.walkUpScopes(resolver, ResolveState.initial(), element)
        return resolver.declaration?.let {
            arrayOf(PsiElementResolveResult(it))
        } ?: emptyArray()
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        element.setName(newElementName)
        return element
    }
}
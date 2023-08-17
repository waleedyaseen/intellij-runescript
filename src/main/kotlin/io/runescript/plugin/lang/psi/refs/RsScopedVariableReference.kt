package io.runescript.plugin.lang.psi.refs

import com.intellij.psi.*
import io.runescript.plugin.lang.psi.RsScopedVariableExpression
import io.runescript.plugin.lang.psi.type.RsPrimitiveType
import io.runescript.plugin.symbollang.psi.index.RsSymbolIndex

class RsScopedVariableReference(element: RsScopedVariableExpression) :
        PsiReferenceBase<RsScopedVariableExpression>(element, element.nameLiteral.textRangeInParent), PsiPolyVariantReference {

    override fun resolve(): PsiElement? {
        val result = multiResolve(false)
        return result.singleOrNull()?.element
    }

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        return scopedVarTypes
            .mapNotNull { RsSymbolIndex.lookup(element.project, it, element.name!!) }
            .map { PsiElementResolveResult(it) }
            .toTypedArray()
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        element.setName(newElementName)
        return element
    }

    companion object {
        private val scopedVarTypes = arrayOf(
            RsPrimitiveType.VARP,
            RsPrimitiveType.VARC,
            RsPrimitiveType.VARBIT
        )
    }
}
package io.runescript.plugin.lang.psi.refs

import com.intellij.psi.*
import io.runescript.plugin.ide.neptune.neptuneModuleData
import io.runescript.plugin.lang.psi.RsScopedVariableExpression
import io.runescript.plugin.symbollang.psi.index.RsSymbolIndex

class RsScopedVariableReference(element: RsScopedVariableExpression) :
        PsiReferenceBase<RsScopedVariableExpression>(element, element.nameLiteral.textRangeInParent), PsiPolyVariantReference {

    override fun resolve(): PsiElement? {
        val result = multiResolve(false)
        return result.singleOrNull()?.element
    }

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val types = element.neptuneModuleData?.types
        val scopedVarTypes = scopedVarTypeLiterals.mapNotNull {
            types?.findOrNull(it)
        }
        return scopedVarTypes
            .mapNotNull { RsSymbolIndex.lookup(element, it, element.name!!) }
            .map { PsiElementResolveResult(it) }
            .toTypedArray()
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        element.setName(newElementName)
        return element
    }

    companion object {
        private val scopedVarTypeLiterals = arrayOf(
            "varp",
            "varc",
            "varbit",
            "varclan",
            "varclansetting"
        )
    }
}
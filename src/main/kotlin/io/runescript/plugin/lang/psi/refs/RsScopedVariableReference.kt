package io.runescript.plugin.lang.psi.refs

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.ResolveResult
import io.runescript.plugin.ide.neptune.neptuneModuleData
import io.runescript.plugin.lang.psi.RsScopedVariableExpression
import io.runescript.plugin.lang.psi.typechecker.type.wrapped.GameVarType
import io.runescript.plugin.symbollang.psi.index.RsSymbolIndex
import io.runescript.plugin.symbollang.psi.rawSymToType

class RsScopedVariableReference(
    element: RsScopedVariableExpression,
) : RsCachedPolyVariantReference<RsScopedVariableExpression>(element, { it.nameLiteral.textRangeInParent }) {
    override fun resolveInner(incompleteCode: Boolean): Array<ResolveResult> {
        val name = element.name ?: return emptyArray()
        val moduleData = element.neptuneModuleData ?: return emptyArray()
        return RsSymbolIndex
            .lookupAll(element, name)
            .filter {
                rawSymToType(
                    it,
                    moduleData.resolvedData.types,
                    moduleData.resolvedData.symbolLoaders,
                ) is GameVarType
            }.map { PsiElementResolveResult(it) }
            .toTypedArray()
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        element.setName(newElementName)
        return element
    }
}

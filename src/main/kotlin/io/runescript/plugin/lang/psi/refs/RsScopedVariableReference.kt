package io.runescript.plugin.lang.psi.refs

import com.intellij.psi.*
import io.runescript.plugin.ide.neptune.neptuneModuleData
import io.runescript.plugin.lang.psi.RsScopedVariableExpression
import io.runescript.plugin.lang.psi.rawSymToType
import io.runescript.plugin.lang.psi.typechecker.type.wrapped.GameVarType
import io.runescript.plugin.symbollang.psi.index.RsSymbolIndex

class RsScopedVariableReference(element: RsScopedVariableExpression) :
    PsiReferenceBase<RsScopedVariableExpression>(element, element.nameLiteral.textRangeInParent),
    PsiPolyVariantReference {

    override fun resolve(): PsiElement? {
        val result = multiResolve(false)
        return result.singleOrNull()?.element
    }

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val name = element.name ?: return emptyArray()
        val moduleData = element.neptuneModuleData ?: return emptyArray()
        return RsSymbolIndex.lookupAll(element, name)
            .filter { rawSymToType(it, moduleData.types, moduleData.symbolLoaders) is GameVarType}
            .map { PsiElementResolveResult(it) }
            .toTypedArray()
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        element.setName(newElementName)
        return element
    }
}
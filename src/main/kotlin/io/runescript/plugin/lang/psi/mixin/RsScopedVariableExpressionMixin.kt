package io.runescript.plugin.lang.psi.mixin

import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.lang.ASTNode
import com.intellij.openapi.module.ModuleUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.SearchScope
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.tree.IElementType
import com.intellij.refactoring.suggested.startOffset
import io.runescript.plugin.lang.psi.RsPsiImplUtil
import io.runescript.plugin.lang.psi.RsScopedVariableExpression
import io.runescript.plugin.lang.psi.refs.RsScopedVariableReference
import io.runescript.plugin.lang.stubs.RsScopedVariableExpressionStub

abstract class RsScopedVariableExpressionMixin : StubBasedPsiElementBase<RsScopedVariableExpressionStub>, RsScopedVariableExpression {

    constructor(node: ASTNode) : super(node)
    constructor(stub: RsScopedVariableExpressionStub, type: IStubElementType<*, *>) : super(stub, type)
    constructor(stub: RsScopedVariableExpressionStub?, type: IElementType?, node: ASTNode?) : super(stub, type, node)

    override fun getUseScope(): SearchScope {
        val module = ModuleUtil.findModuleForPsiElement(this) ?: return super.getUseScope()
        return GlobalSearchScope.moduleScope(module)
    }

    override fun getReference(): PsiReference? {
        return RsScopedVariableReference(this)
    }

    override fun getTextOffset(): Int {
        return nameLiteral.startOffset
    }

    override fun getNameIdentifier(): PsiElement {
        return nameLiteral
    }

    override fun getName(): String {
        return RsPsiImplUtil.getName(nameLiteral)
    }

    override fun setName(newName: String): PsiElement {
        return RsPsiImplUtil.setName(nameLiteral, newName)
    }
}
package io.runescript.plugin.lang.psi.mixin

import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.tree.IElementType
import com.intellij.refactoring.suggested.startOffset
import io.runescript.plugin.lang.psi.RsLocalVariableExpression
import io.runescript.plugin.lang.psi.refs.RsLocalVariableReference
import io.runescript.plugin.lang.stubs.RsLocalVariableExpressionStub

abstract class RsLocalVariableExpressionMixin: StubBasedPsiElementBase<RsLocalVariableExpressionStub>, RsLocalVariableExpression {

    constructor(node: ASTNode) : super(node)
    constructor(stub: RsLocalVariableExpressionStub, type: IStubElementType<*, *>) : super(stub, type)
    constructor(stub: RsLocalVariableExpressionStub?, type: IElementType?, node: ASTNode?) : super(stub, type, node)

    override fun getReference(): PsiReference {
        return RsLocalVariableReference(this)
    }

    override fun getTextOffset(): Int {
        return nameLiteral.startOffset
    }
}
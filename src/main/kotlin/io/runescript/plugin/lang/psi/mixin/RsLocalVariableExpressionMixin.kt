package io.runescript.plugin.lang.psi.mixin

import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.tree.IElementType
import com.intellij.refactoring.suggested.startOffset
import io.runescript.plugin.lang.psi.RsArrayVariableDeclarationStatement
import io.runescript.plugin.lang.psi.RsLocalVariableDeclarationStatement
import io.runescript.plugin.lang.psi.RsLocalVariableExpression
import io.runescript.plugin.lang.psi.RsParameter
import io.runescript.plugin.lang.psi.refs.RsLocalVariableReference
import io.runescript.plugin.lang.stubs.RsLocalVariableExpressionStub

abstract class RsLocalVariableExpressionMixin : StubBasedPsiElementBase<RsLocalVariableExpressionStub>, RsLocalVariableExpression {

    constructor(node: ASTNode) : super(node)
    constructor(stub: RsLocalVariableExpressionStub, type: IStubElementType<*, *>) : super(stub, type)
    constructor(stub: RsLocalVariableExpressionStub?, type: IElementType?, node: ASTNode?) : super(stub, type, node)

    override fun getReference(): PsiReference? {
        // Note(Walied): We need to separate the declaration rule from the assignment rule
        // right now both are using LocalVariableExpression resulting in declaration trying to refer
        // to other variables in parent scopes.
        if (parent !is RsLocalVariableDeclarationStatement &&
                parent !is RsArrayVariableDeclarationStatement &&
                parent !is RsParameter) {
            return RsLocalVariableReference(this)
        }
        return null
    }

    override fun getTextOffset(): Int {
        return nameLiteral.startOffset
    }
}
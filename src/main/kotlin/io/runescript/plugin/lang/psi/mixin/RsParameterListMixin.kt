package io.runescript.plugin.lang.psi.mixin

import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.tree.IElementType
import io.runescript.plugin.lang.psi.RsParameterList
import io.runescript.plugin.lang.stubs.RsParameterListStub

abstract class RsParameterListMixin : StubBasedPsiElementBase<RsParameterListStub>, RsParameterList {

    constructor(node: ASTNode) : super(node)
    constructor(stub: RsParameterListStub, type: IStubElementType<*, *>) : super(stub, type)
    constructor(stub: RsParameterListStub?, type: IElementType?, node: ASTNode?) : super(stub, type, node)

    override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement): Boolean {
        parameterList.forEach {
            if (lastParent != it && !processor.execute(it.localVariableExpression, state)) {
                return false
            }
        }
        return true
    }
}
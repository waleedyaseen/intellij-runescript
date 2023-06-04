package io.runescript.plugin.lang.psi.mixin

import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.tree.IElementType
import io.runescript.plugin.lang.psi.RsScriptHeader
import io.runescript.plugin.lang.stubs.RsScriptHeaderStub

abstract class RsScriptHeaderMixin : StubBasedPsiElementBase<RsScriptHeaderStub>, RsScriptHeader {

    constructor(node: ASTNode) : super(node)
    constructor(stub: RsScriptHeaderStub, type: IStubElementType<*, *>) : super(stub, type)
    constructor(stub: RsScriptHeaderStub?, type: IElementType?, node: ASTNode?) : super(stub, type, node)

    override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement): Boolean {
        parameterList?.let {
            if (!it.processDeclarations(processor, state, lastParent, place)) {
                return false
            }
        }
        return true
    }
}
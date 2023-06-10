package io.runescript.plugin.lang.psi.mixin

import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.lang.ASTNode
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.tree.IElementType
import io.runescript.plugin.lang.psi.RsScriptName
import io.runescript.plugin.lang.stubs.RsScriptNameStub

abstract class RsScriptNameMixin : StubBasedPsiElementBase<RsScriptNameStub>, RsScriptName {

    constructor(node: ASTNode) : super(node)
    constructor(stub: RsScriptNameStub, type: IStubElementType<*, *>) : super(stub, type)
    constructor(stub: RsScriptNameStub?, type: IElementType?, node: ASTNode?) : super(stub, type, node)
}
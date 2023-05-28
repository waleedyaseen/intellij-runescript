package io.runescript.plugin.lang.stubs

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubBase
import com.intellij.psi.stubs.StubElement
import io.runescript.plugin.lang.psi.op.RsOpNameLiteral

class RsOpNameLiteralStub(parent: StubElement<*>?, elementType: IStubElementType<*, *>, val name: String)
    : StubBase<RsOpNameLiteral>(parent, elementType)

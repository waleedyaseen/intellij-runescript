package io.runescript.plugin.oplang.psi.stub

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubBase
import com.intellij.psi.stubs.StubElement
import io.runescript.plugin.oplang.psi.RsOpNameLiteral

class RsOpNameLiteralStub(parent: StubElement<*>?, elementType: IStubElementType<*, *>, val name: String)
    : StubBase<RsOpNameLiteral>(parent, elementType)
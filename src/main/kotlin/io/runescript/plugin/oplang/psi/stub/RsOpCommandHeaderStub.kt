package io.runescript.plugin.oplang.psi.stub

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubBase
import com.intellij.psi.stubs.StubElement
import io.runescript.plugin.oplang.psi.RsOpCommandHeader

class RsOpCommandHeaderStub(parent: StubElement<*>?, elementType: IStubElementType<*, *>)
    : StubBase<RsOpCommandHeader>(parent, elementType)
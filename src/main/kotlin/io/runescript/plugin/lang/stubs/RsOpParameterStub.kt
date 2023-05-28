package io.runescript.plugin.lang.stubs

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubBase
import com.intellij.psi.stubs.StubElement
import io.runescript.plugin.lang.psi.op.RsOpParameter

class RsOpParameterStub(parent: StubElement<*>?, elementType: IStubElementType<*, *>, val name: String)
    : StubBase<RsOpParameter>(parent, elementType)

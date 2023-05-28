package io.runescript.plugin.lang.stubs

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubBase
import com.intellij.psi.stubs.StubElement
import io.runescript.plugin.lang.psi.RsParameter

class RsParameterStub(parent: StubElement<*>?, elementType: IStubElementType<*, *>)
    : StubBase<RsParameter>(parent, elementType)

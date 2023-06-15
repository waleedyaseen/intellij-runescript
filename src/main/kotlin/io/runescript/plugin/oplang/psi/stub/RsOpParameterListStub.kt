package io.runescript.plugin.oplang.psi.stub

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubBase
import com.intellij.psi.stubs.StubElement
import io.runescript.plugin.oplang.psi.RsOpParameterList

class RsOpParameterListStub(parent: StubElement<*>?, elementType: IStubElementType<*, *>)
    : StubBase<RsOpParameterList>(parent, elementType)
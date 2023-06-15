package io.runescript.plugin.oplang.psi.stub

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubBase
import com.intellij.psi.stubs.StubElement
import io.runescript.plugin.oplang.psi.RsOpAttributeList

class RsOpAttributeListStub(parent: StubElement<*>?, elementType: IStubElementType<*, *>)
    : StubBase<RsOpAttributeList>(parent, elementType)
package io.runescript.plugin.oplang.psi.stub

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubBase
import com.intellij.psi.stubs.StubElement
import io.runescript.plugin.oplang.psi.RsOpReturnList

class RsOpReturnListStub(parent: StubElement<*>?, elementType: IStubElementType<*, *>)
    : StubBase<RsOpReturnList>(parent, elementType)
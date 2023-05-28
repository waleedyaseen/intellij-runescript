package io.runescript.plugin.lang.stubs

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubBase
import com.intellij.psi.stubs.StubElement
import io.runescript.plugin.lang.psi.RsReturnList

class RsReturnListStub(parent: StubElement<*>?, elementType: IStubElementType<*, *>)
    : StubBase<RsReturnList>(parent, elementType)

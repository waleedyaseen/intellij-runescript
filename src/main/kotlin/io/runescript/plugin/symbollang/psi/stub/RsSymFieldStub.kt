package io.runescript.plugin.symbollang.psi.stub

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubBase
import com.intellij.psi.stubs.StubElement
import io.runescript.plugin.symbollang.psi.RsSymField

class RsSymFieldStub(parent: StubElement<*>?, elementType: IStubElementType<*, *>, val value: String) :
    StubBase<RsSymField>(parent, elementType)

package io.runescript.plugin.lang.stubs

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubBase
import com.intellij.psi.stubs.StubElement
import io.runescript.plugin.lang.psi.RsNameLiteral

class RsNameLiteralStub(parent: StubElement<*>?, elementType: IStubElementType<*, *>)
    : StubBase<RsNameLiteral>(parent, elementType)

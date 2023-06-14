package io.runescript.plugin.symbollang.psi.stub

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubBase
import com.intellij.psi.stubs.StubElement
import io.runescript.plugin.symbollang.psi.RsSymSymbol

class RsSymSymbolStub(parent: StubElement<*>?, elementType: IStubElementType<*, *>) :
    StubBase<RsSymSymbol>(parent, elementType)

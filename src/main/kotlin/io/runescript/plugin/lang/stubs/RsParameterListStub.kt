package io.runescript.plugin.lang.stubs

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubBase
import com.intellij.psi.stubs.StubElement
import io.runescript.plugin.lang.psi.RsParameterList
import io.runescript.plugin.lang.psi.RsScriptName

class RsParameterListStub(parent: StubElement<*>?, elementType: IStubElementType<*, *>)
    : StubBase<RsParameterList>(parent, elementType)

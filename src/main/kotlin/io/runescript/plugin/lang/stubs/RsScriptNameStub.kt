package io.runescript.plugin.lang.stubs

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubBase
import com.intellij.psi.stubs.StubElement
import io.runescript.plugin.lang.psi.RsScriptName

class RsScriptNameStub(parent: StubElement<*>?, elementType: IStubElementType<*, *>, val triggerName: String, val scriptName: String) : StubBase<RsScriptName>(parent, elementType)

package io.runescript.plugin.lang.stubs

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubBase
import com.intellij.psi.stubs.StubElement
import io.runescript.plugin.lang.psi.RsScript

class RsScriptStub(parent: StubElement<*>?, elementType: IStubElementType<*, *>, val triggerName: String, val scriptName: String) : StubBase<RsScript>(parent, elementType)
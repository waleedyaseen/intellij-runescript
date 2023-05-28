package io.runescript.plugin.lang.stubs.types

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.*
import io.runescript.plugin.lang.RuneScript
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.impl.RsScriptImpl
import io.runescript.plugin.lang.stubs.RsScriptStub

object RsScriptStubType : IStubElementType<RsScriptStub, RsScript>("SCRIPT", RuneScript) {

    override fun getExternalId(): String {
        return "RuneScript.${super.toString()}"
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): RsScriptStub {
        return RsScriptStub(parentStub, this)
    }

    override fun serialize(stub: RsScriptStub, dataStream: StubOutputStream) {
    }

    override fun createStub(psi: RsScript, parentStub: StubElement<out PsiElement>?): RsScriptStub {
        return RsScriptStub(parentStub, this)
    }

    override fun createPsi(stub: RsScriptStub): RsScript {
        return RsScriptImpl(stub, this)
    }

    override fun indexStub(stub: RsScriptStub, sink: IndexSink) {
        // TODO
    }
}
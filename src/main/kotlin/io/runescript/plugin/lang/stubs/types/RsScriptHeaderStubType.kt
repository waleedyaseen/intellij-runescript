package io.runescript.plugin.lang.stubs.types

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.*
import io.runescript.plugin.lang.RuneScript
import io.runescript.plugin.lang.psi.RsScriptHeader
import io.runescript.plugin.lang.psi.impl.RsScriptHeaderImpl
import io.runescript.plugin.lang.stubs.RsScriptHeaderStub

object RsScriptHeaderStubType : IStubElementType<RsScriptHeaderStub, RsScriptHeader>("SCRIPT_HEADER", RuneScript) {

    override fun getExternalId(): String {
        return "RuneScript.${super.toString()}"
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): RsScriptHeaderStub {
        return RsScriptHeaderStub(parentStub, this)
    }

    override fun serialize(stub: RsScriptHeaderStub, dataStream: StubOutputStream) {
    }

    override fun createStub(psi: RsScriptHeader, parentStub: StubElement<out PsiElement>?): RsScriptHeaderStub {
        return RsScriptHeaderStub(parentStub, this)
    }

    override fun createPsi(stub: RsScriptHeaderStub): RsScriptHeader {
        return RsScriptHeaderImpl(stub, this)
    }

    override fun indexStub(stub: RsScriptHeaderStub, sink: IndexSink) {
        // TODO
    }
}
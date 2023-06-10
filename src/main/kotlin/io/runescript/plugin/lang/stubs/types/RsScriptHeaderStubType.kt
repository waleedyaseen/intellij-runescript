package io.runescript.plugin.lang.stubs.types

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import io.runescript.plugin.lang.psi.RsScriptHeader
import io.runescript.plugin.lang.psi.RsStubType
import io.runescript.plugin.lang.psi.impl.RsScriptHeaderImpl
import io.runescript.plugin.lang.stubs.RsScriptHeaderStub

object RsScriptHeaderStubType : RsStubType<RsScriptHeaderStub, RsScriptHeader>("SCRIPT_HEADER") {

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
    }
}
package io.runescript.plugin.lang.stubs.types

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import io.runescript.plugin.lang.psi.RsOpStubType
import io.runescript.plugin.lang.psi.impl.op.RsOpCommandHeaderImpl
import io.runescript.plugin.lang.psi.op.RsOpCommandHeader
import io.runescript.plugin.lang.stubs.RsOpCommandHeaderStub

object RsOpCommandHeaderStubType : RsOpStubType<RsOpCommandHeaderStub, RsOpCommandHeader>("COMMAND_HEADER") {

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): RsOpCommandHeaderStub {
        return RsOpCommandHeaderStub(parentStub, this)
    }

    override fun serialize(stub: RsOpCommandHeaderStub, dataStream: StubOutputStream) {
    }

    override fun createStub(psi: RsOpCommandHeader, parentStub: StubElement<out PsiElement>?): RsOpCommandHeaderStub {
        return RsOpCommandHeaderStub(parentStub, this)
    }

    override fun createPsi(stub: RsOpCommandHeaderStub): RsOpCommandHeader {
        return RsOpCommandHeaderImpl(stub, this)
    }

    override fun indexStub(stub: RsOpCommandHeaderStub, sink: IndexSink) {
    }
}
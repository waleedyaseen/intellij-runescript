package io.runescript.plugin.lang.stubs.types

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import io.runescript.plugin.lang.psi.RsOpStubType
import io.runescript.plugin.lang.psi.impl.op.RsOpParameterImpl
import io.runescript.plugin.lang.psi.op.RsOpParameter
import io.runescript.plugin.lang.stubs.RsOpParameterStub

object RsOpParameterStubType : RsOpStubType<RsOpParameterStub, RsOpParameter>("PARAMETER") {

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): RsOpParameterStub {
        return RsOpParameterStub(parentStub, this, dataStream.readUTF())
    }

    override fun serialize(stub: RsOpParameterStub, dataStream: StubOutputStream) {
        dataStream.writeUTF(stub.name)
    }

    override fun createStub(psi: RsOpParameter, parentStub: StubElement<out PsiElement>?): RsOpParameterStub {
        return RsOpParameterStub(parentStub, this, psi.nameLiteral.text)
    }

    override fun createPsi(stub: RsOpParameterStub): RsOpParameter {
        return RsOpParameterImpl(stub, this)
    }

    override fun indexStub(stub: RsOpParameterStub, sink: IndexSink) {
    }
}
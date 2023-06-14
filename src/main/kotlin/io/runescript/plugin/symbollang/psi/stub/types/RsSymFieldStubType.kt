package io.runescript.plugin.symbollang.psi.stub.types

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import io.runescript.plugin.symbollang.psi.RsSymField
import io.runescript.plugin.symbollang.psi.impl.RsSymFieldImpl
import io.runescript.plugin.symbollang.psi.stub.RsSymFieldStub

object RsSymFieldStubType
    : RsSymStubType<RsSymFieldStub, RsSymField>("FIELD") {

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): RsSymFieldStub {
        return RsSymFieldStub(parentStub, this, dataStream.readUTF())
    }

    override fun serialize(stub: RsSymFieldStub, dataStream: StubOutputStream) {
        dataStream.writeUTF(stub.value)
    }

    override fun createStub(psi: RsSymField, parentStub: StubElement<out PsiElement>?): RsSymFieldStub {
        return RsSymFieldStub(parentStub, this, psi.text)
    }

    override fun createPsi(stub: RsSymFieldStub): RsSymField {
        return RsSymFieldImpl(stub, this)
    }

    override fun indexStub(stub: RsSymFieldStub, sink: IndexSink) {

    }
}
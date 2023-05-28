package io.runescript.plugin.lang.stubs.types

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import io.runescript.plugin.lang.psi.RsOpStubType
import io.runescript.plugin.lang.psi.impl.op.RsOpTypeNameImpl
import io.runescript.plugin.lang.psi.op.RsOpTypeName
import io.runescript.plugin.lang.stubs.RsOpTypeNameStub

object RsOpTypeNameStubType : RsOpStubType<RsOpTypeNameStub, RsOpTypeName>("TYPE_NAME") {

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): RsOpTypeNameStub {
        return RsOpTypeNameStub(parentStub, this, dataStream.readUTF())
    }

    override fun serialize(stub: RsOpTypeNameStub, dataStream: StubOutputStream) {
        dataStream.writeUTF(stub.name)
    }

    override fun createStub(psi: RsOpTypeName, parentStub: StubElement<out PsiElement>?): RsOpTypeNameStub {
        return RsOpTypeNameStub(parentStub, this, psi.text)
    }

    override fun createPsi(stub: RsOpTypeNameStub): RsOpTypeName {
        return RsOpTypeNameImpl(stub, this)
    }

    override fun indexStub(stub: RsOpTypeNameStub, sink: IndexSink) {
    }
}
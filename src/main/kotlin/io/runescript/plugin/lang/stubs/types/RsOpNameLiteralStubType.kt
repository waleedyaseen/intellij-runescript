package io.runescript.plugin.lang.stubs.types

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import io.runescript.plugin.lang.psi.RsOpStubType
import io.runescript.plugin.lang.psi.impl.op.RsOpNameLiteralImpl
import io.runescript.plugin.lang.psi.op.RsOpNameLiteral
import io.runescript.plugin.lang.stubs.RsOpNameLiteralStub

object RsOpNameLiteralStubType : RsOpStubType<RsOpNameLiteralStub, RsOpNameLiteral>("NAME_LITERAL") {

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): RsOpNameLiteralStub {
        return RsOpNameLiteralStub(parentStub, this, dataStream.readUTF())
    }

    override fun serialize(stub: RsOpNameLiteralStub, dataStream: StubOutputStream) {
        dataStream.writeUTF(stub.name)
    }

    override fun createStub(psi: RsOpNameLiteral, parentStub: StubElement<out PsiElement>?): RsOpNameLiteralStub {
        return RsOpNameLiteralStub(parentStub, this, psi.text)
    }

    override fun createPsi(stub: RsOpNameLiteralStub): RsOpNameLiteral {
        return RsOpNameLiteralImpl(stub, this)
    }

    override fun indexStub(stub: RsOpNameLiteralStub, sink: IndexSink) {
    }
}
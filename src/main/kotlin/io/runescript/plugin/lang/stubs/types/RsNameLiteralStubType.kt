package io.runescript.plugin.lang.stubs.types

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import io.runescript.plugin.lang.psi.RsNameLiteral
import io.runescript.plugin.lang.psi.RsStubType
import io.runescript.plugin.lang.psi.impl.RsNameLiteralImpl
import io.runescript.plugin.lang.stubs.RsNameLiteralStub

object RsNameLiteralStubType : RsStubType<RsNameLiteralStub, RsNameLiteral>("NAME_LITERAL") {

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): RsNameLiteralStub {
        return RsNameLiteralStub(parentStub, this)
    }

    override fun serialize(stub: RsNameLiteralStub, dataStream: StubOutputStream) {
    }

    override fun createStub(psi: RsNameLiteral, parentStub: StubElement<out PsiElement>?): RsNameLiteralStub {
        return RsNameLiteralStub(parentStub, this)
    }

    override fun createPsi(stub: RsNameLiteralStub): RsNameLiteral {
        return RsNameLiteralImpl(stub, this)
    }

    override fun indexStub(stub: RsNameLiteralStub, sink: IndexSink) {
        // TODO
    }
}
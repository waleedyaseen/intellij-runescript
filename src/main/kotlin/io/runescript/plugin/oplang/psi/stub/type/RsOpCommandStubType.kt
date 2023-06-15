package io.runescript.plugin.oplang.psi.stub.type

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import io.runescript.plugin.oplang.psi.RsOpCommand
import io.runescript.plugin.oplang.psi.RsOpStubType
import io.runescript.plugin.oplang.psi.impl.RsOpCommandImpl
import io.runescript.plugin.oplang.psi.index.RsCommandIndex
import io.runescript.plugin.oplang.psi.stub.RsOpCommandStub
import io.runescript.plugin.oplang.psi.stub.RsOpNameLiteralStub

object RsOpCommandStubType : RsOpStubType<RsOpCommandStub, RsOpCommand>("COMMAND") {

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): RsOpCommandStub {
        return RsOpCommandStub(parentStub, this)
    }

    override fun serialize(stub: RsOpCommandStub, dataStream: StubOutputStream) {
    }

    override fun createStub(psi: RsOpCommand, parentStub: StubElement<out PsiElement>?): RsOpCommandStub {
        return RsOpCommandStub(parentStub, this)
    }

    override fun createPsi(stub: RsOpCommandStub): RsOpCommand {
        return RsOpCommandImpl(stub, this)
    }

    override fun indexStub(stub: RsOpCommandStub, sink: IndexSink) {
        val header = stub.findChildStubByType(RsOpCommandHeaderStubType)!!
        val mum = header.childrenStubs[1] as RsOpNameLiteralStub
        sink.occurrence(RsCommandIndex.KEY, mum.name)
    }
}
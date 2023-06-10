package io.runescript.plugin.lang.stubs.types

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.RsStubType
import io.runescript.plugin.lang.psi.impl.RsScriptImpl
import io.runescript.plugin.lang.psi.scriptName
import io.runescript.plugin.lang.psi.triggerName
import io.runescript.plugin.lang.stubs.RsScriptStub
import io.runescript.plugin.lang.stubs.index.RsGotoScriptIndex

object RsScriptStubType : RsStubType<RsScriptStub, RsScript>("SCRIPT") {

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): RsScriptStub {
        return RsScriptStub(parentStub, this, dataStream.readUTF(), dataStream.readUTF())
    }

    override fun serialize(stub: RsScriptStub, dataStream: StubOutputStream) {
        dataStream.writeUTF(stub.triggerName)
        dataStream.writeUTF(stub.scriptName)
    }

    override fun createStub(psi: RsScript, parentStub: StubElement<out PsiElement>?): RsScriptStub {
        return RsScriptStub(parentStub, this, psi.triggerName, psi.scriptName)
    }

    override fun createPsi(stub: RsScriptStub): RsScript {
        return RsScriptImpl(stub, this)
    }

    override fun indexStub(stub: RsScriptStub, sink: IndexSink) {
        if (stub.triggerName == "proc") {
            sink.occurrence(RsGotoScriptIndex.KEY, stub.scriptName)
        }
    }
}
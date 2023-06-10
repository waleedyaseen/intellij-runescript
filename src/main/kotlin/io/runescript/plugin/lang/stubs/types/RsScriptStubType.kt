package io.runescript.plugin.lang.stubs.types

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.RsStubType
import io.runescript.plugin.lang.psi.impl.RsScriptImpl
import io.runescript.plugin.lang.stubs.RsScriptStub
import io.runescript.plugin.lang.stubs.index.RsGotoScriptIndex

object RsScriptStubType : RsStubType<RsScriptStub, RsScript>("SCRIPT") {

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): RsScriptStub {
        return RsScriptStub(parentStub, this)
    }

    override fun serialize(stub: RsScriptStub, dataStream: StubOutputStream) {
    }

    override fun createStub(psi: RsScript, parentStub: StubElement<out PsiElement>?): RsScriptStub {
        return RsScriptStub(parentStub, this)
    }

    override fun createPsi(stub: RsScriptStub): RsScript {
        return RsScriptImpl(stub, this)
    }

    override fun indexStub(stub: RsScriptStub, sink: IndexSink) {
        val header = stub.findChildStubByType(RsScriptHeaderStubType)!!
        val name = header.findChildStubByType(RsScriptNameStubType)!!
        if (name.triggerName == "proc") {
            sink.occurrence(RsGotoScriptIndex.KEY, name.scriptName)
        }
    }
}
package io.runescript.plugin.lang.stubs.types

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.*
import io.runescript.plugin.lang.RuneScript
import io.runescript.plugin.lang.psi.RsScriptName
import io.runescript.plugin.lang.psi.impl.RsScriptNameImpl
import io.runescript.plugin.lang.stubs.RsScriptNameStub
import io.runescript.plugin.lang.stubs.index.RsGotoScriptIndex

object RsScriptNameStubType : IStubElementType<RsScriptNameStub, RsScriptName>("SCRIPT_NAME", RuneScript) {

    override fun getExternalId(): String {
        return "RuneScript.${super.toString()}"
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): RsScriptNameStub {
        return RsScriptNameStub(parentStub, this, dataStream.readUTF(), dataStream.readUTF())
    }

    override fun serialize(stub: RsScriptNameStub, dataStream: StubOutputStream) {
        dataStream.writeUTF(stub.triggerName)
        dataStream.writeUTF(stub.scriptName)
    }

    override fun createStub(psi: RsScriptName, parentStub: StubElement<out PsiElement>?): RsScriptNameStub {
        return RsScriptNameStub(parentStub, this, psi.triggerExpression.text, psi.nameExpression!!.text)
    }

    override fun createPsi(stub: RsScriptNameStub): RsScriptName {
        return RsScriptNameImpl(stub, this)
    }

    override fun indexStub(stub: RsScriptNameStub, sink: IndexSink) {
    }
}
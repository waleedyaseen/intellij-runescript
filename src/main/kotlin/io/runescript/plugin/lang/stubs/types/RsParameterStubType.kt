package io.runescript.plugin.lang.stubs.types

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.*
import io.runescript.plugin.lang.RuneScript
import io.runescript.plugin.lang.psi.RsParameter
import io.runescript.plugin.lang.psi.impl.RsParameterImpl
import io.runescript.plugin.lang.stubs.RsParameterStub

object RsParameterStubType : IStubElementType<RsParameterStub, RsParameter>("PARAMETER", RuneScript) {

    override fun getExternalId(): String {
        return "RuneScript.${super.toString()}"
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): RsParameterStub {
        return RsParameterStub(parentStub, this)
    }

    override fun serialize(stub: RsParameterStub, dataStream: StubOutputStream) {
    }

    override fun createStub(psi: RsParameter, parentStub: StubElement<out PsiElement>?): RsParameterStub {
        return RsParameterStub(parentStub, this)
    }

    override fun createPsi(stub: RsParameterStub): RsParameter {
        return RsParameterImpl(stub, this)
    }

    override fun indexStub(stub: RsParameterStub, sink: IndexSink) {
        // TODO
    }
}
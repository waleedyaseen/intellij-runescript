package io.runescript.plugin.lang.stubs.types

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.*
import io.runescript.plugin.lang.RuneScript
import io.runescript.plugin.lang.psi.RsParameterList
import io.runescript.plugin.lang.psi.impl.RsParameterListImpl
import io.runescript.plugin.lang.stubs.RsParameterListStub

object RsParameterListStubType : IStubElementType<RsParameterListStub, RsParameterList>("PARAMETER_LIST", RuneScript) {

    override fun getExternalId(): String {
        return "RuneScript.${super.toString()}"
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): RsParameterListStub {
        return RsParameterListStub(parentStub, this)
    }

    override fun serialize(stub: RsParameterListStub, dataStream: StubOutputStream) {
    }

    override fun createStub(psi: RsParameterList, parentStub: StubElement<out PsiElement>?): RsParameterListStub {
        return RsParameterListStub(parentStub, this)
    }

    override fun createPsi(stub: RsParameterListStub): RsParameterList {
        return RsParameterListImpl(stub, this)
    }

    override fun indexStub(stub: RsParameterListStub, sink: IndexSink) {
        // TODO
    }
}
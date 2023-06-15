package io.runescript.plugin.oplang.psi.stub.type

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import io.runescript.plugin.oplang.psi.RsOpParameterList
import io.runescript.plugin.oplang.psi.RsOpStubType
import io.runescript.plugin.oplang.psi.impl.RsOpParameterListImpl
import io.runescript.plugin.oplang.psi.stub.RsOpParameterListStub

object RsOpParameterListStubType : RsOpStubType<RsOpParameterListStub, RsOpParameterList>("PARAMETER_LIST") {

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): RsOpParameterListStub {
        return RsOpParameterListStub(parentStub, this)
    }

    override fun serialize(stub: RsOpParameterListStub, dataStream: StubOutputStream) {
    }

    override fun createStub(psi: RsOpParameterList, parentStub: StubElement<out PsiElement>?): RsOpParameterListStub {
        return RsOpParameterListStub(parentStub, this)
    }

    override fun createPsi(stub: RsOpParameterListStub): RsOpParameterList {
        return RsOpParameterListImpl(stub, this)
    }

    override fun indexStub(stub: RsOpParameterListStub, sink: IndexSink) {
    }
}
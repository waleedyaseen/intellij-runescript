package io.runescript.plugin.lang.stubs.types

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import io.runescript.plugin.lang.psi.RsOpStubType
import io.runescript.plugin.lang.psi.impl.op.RsOpParameterListImpl
import io.runescript.plugin.lang.psi.op.RsOpParameterList
import io.runescript.plugin.lang.stubs.RsOpParameterListStub

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
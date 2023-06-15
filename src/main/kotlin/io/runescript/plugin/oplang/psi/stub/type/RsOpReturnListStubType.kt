package io.runescript.plugin.oplang.psi.stub.type

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import io.runescript.plugin.oplang.psi.RsOpReturnList
import io.runescript.plugin.oplang.psi.RsOpStubType
import io.runescript.plugin.oplang.psi.impl.RsOpReturnListImpl
import io.runescript.plugin.oplang.psi.stub.RsOpReturnListStub

object RsOpReturnListStubType : RsOpStubType<RsOpReturnListStub, RsOpReturnList>("RETURN_LIST") {

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): RsOpReturnListStub {
        return RsOpReturnListStub(parentStub, this)
    }

    override fun serialize(stub: RsOpReturnListStub, dataStream: StubOutputStream) {
    }

    override fun createStub(psi: RsOpReturnList, parentStub: StubElement<out PsiElement>?): RsOpReturnListStub {
        return RsOpReturnListStub(parentStub, this)
    }

    override fun createPsi(stub: RsOpReturnListStub): RsOpReturnList {
        return RsOpReturnListImpl(stub, this)
    }

    override fun indexStub(stub: RsOpReturnListStub, sink: IndexSink) {
    }
}
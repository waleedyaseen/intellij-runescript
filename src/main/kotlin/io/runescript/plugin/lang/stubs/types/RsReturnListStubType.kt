package io.runescript.plugin.lang.stubs.types

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import io.runescript.plugin.lang.psi.RsReturnList
import io.runescript.plugin.lang.psi.RsStubType
import io.runescript.plugin.lang.psi.impl.RsReturnListImpl
import io.runescript.plugin.lang.stubs.RsReturnListStub

object RsReturnListStubType : RsStubType<RsReturnListStub, RsReturnList>("RETURN_LIST") {

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): RsReturnListStub {
        return RsReturnListStub(parentStub, this)
    }

    override fun serialize(stub: RsReturnListStub, dataStream: StubOutputStream) {
    }

    override fun createStub(psi: RsReturnList, parentStub: StubElement<out PsiElement>?): RsReturnListStub {
        return RsReturnListStub(parentStub, this)
    }

    override fun createPsi(stub: RsReturnListStub): RsReturnList {
        return RsReturnListImpl(stub, this)
    }

    override fun indexStub(stub: RsReturnListStub, sink: IndexSink) {
        // TODO
    }
}
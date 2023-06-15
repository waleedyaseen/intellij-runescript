package io.runescript.plugin.oplang.psi.stub.type

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import io.runescript.plugin.oplang.psi.RsOpAttributeList
import io.runescript.plugin.oplang.psi.RsOpStubType
import io.runescript.plugin.oplang.psi.impl.RsOpAttributeListImpl
import io.runescript.plugin.oplang.psi.stub.RsOpAttributeListStub

object RsOpAttributeListStubType : RsOpStubType<RsOpAttributeListStub, RsOpAttributeList>("ATTRIBUTE_LIST") {

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): RsOpAttributeListStub {
        return RsOpAttributeListStub(parentStub, this)
    }

    override fun serialize(stub: RsOpAttributeListStub, dataStream: StubOutputStream) {
    }

    override fun createStub(psi: RsOpAttributeList, parentStub: StubElement<out PsiElement>?): RsOpAttributeListStub {
        return RsOpAttributeListStub(parentStub, this)
    }

    override fun createPsi(stub: RsOpAttributeListStub): RsOpAttributeList {
        return RsOpAttributeListImpl(stub, this)
    }

    override fun indexStub(stub: RsOpAttributeListStub, sink: IndexSink) {
    }
}
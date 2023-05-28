package io.runescript.plugin.lang.stubs.types

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.*
import io.runescript.plugin.lang.RuneScript
import io.runescript.plugin.lang.psi.RsTypeName
import io.runescript.plugin.lang.psi.impl.RsTypeNameImpl
import io.runescript.plugin.lang.stubs.RsTypeNameStub

object RsTypeNameStubType : IStubElementType<RsTypeNameStub, RsTypeName>("TYPE_NAME", RuneScript) {

    override fun getExternalId(): String {
        return "RuneScript.${super.toString()}"
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): RsTypeNameStub {
        return RsTypeNameStub(parentStub, this)
    }

    override fun serialize(stub: RsTypeNameStub, dataStream: StubOutputStream) {
    }

    override fun createStub(psi: RsTypeName, parentStub: StubElement<out PsiElement>?): RsTypeNameStub {
        return RsTypeNameStub(parentStub, this)
    }

    override fun createPsi(stub: RsTypeNameStub): RsTypeName {
        return RsTypeNameImpl(stub, this)
    }

    override fun indexStub(stub: RsTypeNameStub, sink: IndexSink) {
        // TODO
    }
}
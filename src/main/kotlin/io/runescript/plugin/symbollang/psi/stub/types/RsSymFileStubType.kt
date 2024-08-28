package io.runescript.plugin.symbollang.psi.stub.types

import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import com.intellij.psi.tree.IStubFileElementType
import io.runescript.plugin.symbollang.RuneScriptSymbol
import io.runescript.plugin.symbollang.psi.stub.RsSymFileStub

object RsSymFileStubType : IStubFileElementType<RsSymFileStub>(RuneScriptSymbol) {

    override fun getStubVersion() = 1

    override fun serialize(stub: RsSymFileStub, dataStream: StubOutputStream) {

    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): RsSymFileStub {
        return RsSymFileStub(null)
    }

    override fun getExternalId() = "RuneScriptSymbol.file"
}
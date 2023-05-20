package io.runescript.plugin.lang.stubs.types

import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import com.intellij.psi.tree.IStubFileElementType
import io.runescript.plugin.lang.RuneScript
import io.runescript.plugin.lang.stubs.RsFileStub

object RsFileStubType : IStubFileElementType<RsFileStub>(RuneScript) {

    override fun getStubVersion() = 0

    override fun serialize(stub: RsFileStub, dataStream: StubOutputStream) {

    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): RsFileStub {
        return RsFileStub(null)
    }

    override fun getExternalId() = "RuneScript.file"
}
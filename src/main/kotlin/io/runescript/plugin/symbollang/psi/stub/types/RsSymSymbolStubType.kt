package io.runescript.plugin.symbollang.psi.stub.types

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import io.runescript.plugin.symbollang.psi.RsSymSymbol
import io.runescript.plugin.symbollang.psi.impl.RsSymSymbolImpl
import io.runescript.plugin.symbollang.psi.index.RsSymbolIndex
import io.runescript.plugin.symbollang.psi.isConstantFile
import io.runescript.plugin.symbollang.psi.stub.RsSymFieldStub
import io.runescript.plugin.symbollang.psi.stub.RsSymSymbolStub

object RsSymSymbolStubType
    : RsSymStubType<RsSymSymbolStub, RsSymSymbol>("SYMBOL") {

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): RsSymSymbolStub {
        return RsSymSymbolStub(parentStub, this)
    }

    override fun serialize(stub: RsSymSymbolStub, dataStream: StubOutputStream) {
    }

    override fun createStub(psi: RsSymSymbol, parentStub: StubElement<out PsiElement>?): RsSymSymbolStub {
        return RsSymSymbolStub(parentStub, this)
    }

    override fun createPsi(stub: RsSymSymbolStub): RsSymSymbol {
        return RsSymSymbolImpl(stub, this)
    }

    override fun indexStub(stub: RsSymSymbolStub, sink: IndexSink) {
        val nameField = if (stub.psi.containingFile.virtualFile.isConstantFile()) {
            stub.childrenStubs[0] as RsSymFieldStub
        } else {
            stub.childrenStubs[1] as RsSymFieldStub
        }
        sink.occurrence(RsSymbolIndex.KEY, nameField.value)
    }
}
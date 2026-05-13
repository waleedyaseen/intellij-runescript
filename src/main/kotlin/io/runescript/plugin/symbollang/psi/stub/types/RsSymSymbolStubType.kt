package io.runescript.plugin.symbollang.psi.stub.types

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import io.runescript.plugin.symbollang.psi.RsSymSymbol
import io.runescript.plugin.symbollang.psi.impl.RsSymSymbolImpl
import io.runescript.plugin.symbollang.psi.index.RsSymbolIndex
import io.runescript.plugin.symbollang.psi.index.RsSymbolTypeIndex
import io.runescript.plugin.symbollang.psi.isConstantFile
import io.runescript.plugin.symbollang.psi.resolveToSymTypeName
import io.runescript.plugin.symbollang.psi.stub.RsSymFieldStub
import io.runescript.plugin.symbollang.psi.stub.RsSymSymbolStub

object RsSymSymbolStubType :
    RsSymStubType<RsSymSymbolStub, RsSymSymbol>("SYMBOL") {
    override fun deserialize(
        dataStream: StubInputStream,
        parentStub: StubElement<*>?,
    ): RsSymSymbolStub = RsSymSymbolStub(parentStub, this)

    override fun serialize(
        stub: RsSymSymbolStub,
        dataStream: StubOutputStream,
    ) {
    }

    override fun createStub(
        psi: RsSymSymbol,
        parentStub: StubElement<out PsiElement>?,
    ): RsSymSymbolStub = RsSymSymbolStub(parentStub, this)

    override fun createPsi(stub: RsSymSymbolStub): RsSymSymbol = RsSymSymbolImpl(stub, this)

    override fun indexStub(
        stub: RsSymSymbolStub,
        sink: IndexSink,
    ) {
        val nameField =
            if (stub.psi.containingFile.isConstantFile()) {
                stub.childrenStubs[0] as RsSymFieldStub
            } else {
                stub.childrenStubs[1] as RsSymFieldStub
            }
        sink.occurrence(RsSymbolIndex.KEY, nameField.value)
        val typeName = resolveToSymTypeName(stub.psi.containingFile)
        if (typeName != null) {
            sink.occurrence(RsSymbolTypeIndex.KEY, RsSymbolTypeIndex.makeKey(typeName, nameField.value))
        }
        if (typeName in VAR_SYMBOL_TYPES && stub.childrenStubs.size >= 3) {
            val valueTypeField = stub.childrenStubs[2] as RsSymFieldStub
            if (valueTypeField.value.isNotBlank()) {
                sink.occurrence(RsSymbolTypeIndex.KEY, RsSymbolTypeIndex.makeScopedVarKey(valueTypeField.value, nameField.value))
            }
        }
    }

    private val VAR_SYMBOL_TYPES = setOf("varbit", "varc", "varclan", "varclansetting", "varcstr", "varp")
}

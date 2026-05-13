package io.runescript.plugin.symbollang.psi.index

import com.intellij.psi.stubs.StringStubIndexExtension
import com.intellij.psi.stubs.StubIndexKey
import io.runescript.plugin.symbollang.psi.RsSymSymbol
import io.runescript.plugin.symbollang.psi.stub.types.RsSymFileStubType

class RsSymbolTypeIndex : StringStubIndexExtension<RsSymSymbol>() {
    override fun getVersion(): Int = RsSymFileStubType.stubVersion

    override fun getKey(): StubIndexKey<String, RsSymSymbol> = KEY

    companion object {
        val KEY =
            StubIndexKey.createIndexKey<String, RsSymSymbol>("io.runescript.plugin.symbollang.psi.index.RsSymbolTypeIndex")

        fun makeKey(
            typeName: String,
            symbolName: String,
        ): String = "$typeName\t$symbolName"

        fun typePrefix(typeName: String): String = "$typeName\t"

        fun makeScopedVarKey(
            typeName: String,
            symbolName: String,
        ): String = "scoped:$typeName\t$symbolName"

        fun scopedVarTypePrefix(typeName: String): String = "scoped:$typeName\t"
    }
}

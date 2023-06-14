package io.runescript.plugin.symbollang.psi.stub

import com.intellij.psi.stubs.IStubElementType
import io.runescript.plugin.symbollang.psi.stub.types.RsSymFieldStubType
import io.runescript.plugin.symbollang.psi.stub.types.RsSymSymbolStubType

object RsSymStubElementFactory {

    @JvmStatic
    fun create(name: String): IStubElementType<*, *> {
        return when (name) {
            "SYMBOL" -> RsSymSymbolStubType
            "FIELD" -> RsSymFieldStubType
            else -> error("Unrecognized stub type: $name")
        }
    }
}
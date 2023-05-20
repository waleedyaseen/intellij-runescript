package io.runescript.plugin.lang.stubs

import com.intellij.psi.stubs.IStubElementType
import io.runescript.plugin.lang.stubs.types.RsScriptHeaderStubType
import io.runescript.plugin.lang.stubs.types.RsScriptNameStubType
import io.runescript.plugin.lang.stubs.types.RsScriptStubType

object StubElementTypeFactory {

    @JvmStatic
    fun create(name: String): IStubElementType<*, *> {
        return when(name) {
            "SCRIPT" -> RsScriptStubType
            "SCRIPT_HEADER" -> RsScriptHeaderStubType
            "SCRIPT_NAME" -> RsScriptNameStubType
            else -> error("Unrecognized stub type: $name")
        }
    }
}
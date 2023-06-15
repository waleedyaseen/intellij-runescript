package io.runescript.plugin.oplang.psi.stub

import com.intellij.psi.stubs.IStubElementType
import io.runescript.plugin.oplang.psi.stub.type.*

object RsOpStubElementTypeFactory {

    @JvmStatic
    fun create(name: String): IStubElementType<*, *> {
        return when (name) {
            "COMMAND" -> RsOpCommandStubType
            "COMMAND_HEADER" -> RsOpCommandHeaderStubType
            "RETURN_LIST" -> RsOpReturnListStubType
            "PARAMETER_LIST" -> RsOpParameterListStubType
            "PARAMETER" -> RsOpParameterStubType
            "ATTRIBUTE_LIST" -> RsOpAttributeListStubType
            "NAME_LITERAL" -> RsOpNameLiteralStubType
            "TYPE_NAME" -> RsOpTypeNameStubType
            else -> error("Unrecognized stub type: $name")
        }
    }
}
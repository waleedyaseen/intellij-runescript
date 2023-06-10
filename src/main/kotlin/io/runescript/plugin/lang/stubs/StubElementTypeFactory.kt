package io.runescript.plugin.lang.stubs

import com.intellij.psi.stubs.IStubElementType
import io.runescript.plugin.lang.stubs.types.*

object StubElementTypeFactory {

    @JvmStatic
    fun create(name: String): IStubElementType<*, *> {
        return when (name) {
            "SCRIPT" -> RsScriptStubType
            "PARAMETER_LIST" -> RsParameterListStubType
            "PARAMETER" -> RsParameterStubType
            "RETURN_LIST" -> RsReturnListStubType
            "TYPE_NAME" -> RsTypeNameStubType
            "LOCAL_VARIABLE_EXPRESSION" -> RsLocalVariableExpressionStubType
            "NAME_LITERAL" -> RsNameLiteralStubType
            else -> error("Unrecognized stub type: $name")
        }
    }
}
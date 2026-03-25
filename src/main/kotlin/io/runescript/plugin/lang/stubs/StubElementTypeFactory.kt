package io.runescript.plugin.lang.stubs

import com.intellij.psi.stubs.IStubElementType
import io.runescript.plugin.lang.stubs.types.RsLocalVariableExpressionStubType
import io.runescript.plugin.lang.stubs.types.RsNameLiteralStubType
import io.runescript.plugin.lang.stubs.types.RsParameterListStubType
import io.runescript.plugin.lang.stubs.types.RsParameterStubType
import io.runescript.plugin.lang.stubs.types.RsReturnListStubType
import io.runescript.plugin.lang.stubs.types.RsScopedVariableExpressionStubType
import io.runescript.plugin.lang.stubs.types.RsScriptStubType
import io.runescript.plugin.lang.stubs.types.RsTypeNameStubType

object StubElementTypeFactory {
    @JvmStatic
    fun create(name: String): IStubElementType<*, *> =
        when (name) {
            "SCRIPT" -> RsScriptStubType
            "PARAMETER_LIST" -> RsParameterListStubType
            "PARAMETER" -> RsParameterStubType
            "RETURN_LIST" -> RsReturnListStubType
            "TYPE_NAME" -> RsTypeNameStubType
            "LOCAL_VARIABLE_EXPRESSION" -> RsLocalVariableExpressionStubType
            "SCOPED_VARIABLE_EXPRESSION" -> RsScopedVariableExpressionStubType
            "NAME_LITERAL" -> RsNameLiteralStubType
            else -> error("Unrecognized stub type: $name")
        }
}

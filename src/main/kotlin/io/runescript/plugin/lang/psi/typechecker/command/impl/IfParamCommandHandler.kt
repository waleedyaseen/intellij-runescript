package io.runescript.plugin.lang.psi.typechecker.command.impl

import io.runescript.plugin.lang.psi.typechecker.TypeCheckingContext
import io.runescript.plugin.lang.psi.typechecker.command.DynamicCommandHandler
import io.runescript.plugin.lang.psi.typechecker.type
import io.runescript.plugin.lang.psi.typechecker.type.MetaType
import io.runescript.plugin.lang.psi.typechecker.type.ParamType
import io.runescript.plugin.lang.psi.typechecker.type.PrimitiveType
import io.runescript.plugin.lang.psi.typechecker.type.ScriptVarType
import io.runescript.plugin.lang.psi.typechecker.type.TupleType

/**
 * An implementation of [DynamicCommandHandler] that adds support for type checking
 * the `if_param` and `cc_param` commands within scripts.
 *
 * Example:
 * ```
 * def_int $i = if_param(some_int_param, $comp, null);
 * def_int $j = cc_param(some_int_param);
 * ```
 */
class IfParamCommandHandler(private val cc: Boolean) : DynamicCommandHandler {
    override fun TypeCheckingContext.typeCheck() {
        // check param reference
        val paramExpr = checkArgument(0, ParamCommandHandler.PARAM_ANY)
        val paramReturnType = (paramExpr?.type as? ParamType)?.inner
        if (!cc) {
            checkArgument(1, ScriptVarType.COMPONENT)
            checkArgument(2, PrimitiveType.INT)
        }

        // define the expected types based on what is currently known
        val expectedTypes = if (!cc) {
            IF_BASE_EXPECTED_TYPES
        } else {
            CC_BASE_EXPECTED_TYPES
        }

        // compare the expected types with the actual types
        if (!checkArgumentTypes(expectedTypes)) {
            expression.type = MetaType.Error
            return
        }

        // verify the param type was defined
        if (paramReturnType == null) {
            expression.reportError(diagnostics, "Param return type was not able to be found.")
            expression.type = MetaType.Error
            return
        }

        expression.type = paramReturnType
    }

    private companion object {
        val IF_BASE_EXPECTED_TYPES = TupleType(
            ParamCommandHandler.PARAM_ANY,
            ScriptVarType.COMPONENT,
            PrimitiveType.INT,
        )
        val CC_BASE_EXPECTED_TYPES = ParamCommandHandler.PARAM_ANY
    }
}

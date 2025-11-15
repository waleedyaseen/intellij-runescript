package io.runescript.plugin.lang.psi.typechecker.command.impl.debug


import io.runescript.plugin.lang.psi.typechecker.TypeCheckingContext
import io.runescript.plugin.lang.psi.typechecker.command.DynamicCommandHandler
import io.runescript.plugin.lang.psi.typechecker.type
import io.runescript.plugin.lang.psi.typechecker.type.BaseVarType
import io.runescript.plugin.lang.psi.typechecker.type.MetaType
import io.runescript.plugin.lang.psi.typechecker.type.PrimitiveType
import io.runescript.plugin.lang.psi.typechecker.type.TupleType

/**
 * Developer "command" used to easily debug any expression.
 *
 * Converts `dump(expr1, ...)` into the string `"expr1=<expr1>, ..."`, converting types where needed.
 */
class DumpCommandHandler : DynamicCommandHandler {
    override fun TypeCheckingContext.typeCheck() {
        if (expression.arguments.isEmpty()) {
            expression.reportError(diagnostics, DIAGNOSTIC_INVALID_SIGNATURE)
        } else {
            for ((i, arg) in expression.arguments.withIndex()) {
                checkArgument(i, MetaType.Any)

                val type = arg.type
                if (type is TupleType) {
                    arg.reportError(diagnostics, DIAGNOSTIC_TUPLE_TYPE, type.representation)
                } else if (type.baseType != BaseVarType.INTEGER && type.baseType != BaseVarType.STRING) {
                    arg.reportError(diagnostics, DIAGNOSTIC_INVALID_BASE_TYPE, type.representation)
                } else if (type == MetaType.Unit) {
                    arg.reportError(diagnostics, DIAGNOSTIC_UNIT_TYPE, type.representation)
                }
            }
        }

        expression.type = PrimitiveType.STRING
    }

    private companion object {
        const val DIAGNOSTIC_INVALID_SIGNATURE = "Type mismatch: '<unit>' was given but 'any...' was expected."
        const val DIAGNOSTIC_TUPLE_TYPE = "Unable to dump multi-value types: %s"
        const val DIAGNOSTIC_INVALID_BASE_TYPE = "Unable to debug '%s' expressions."
        const val DIAGNOSTIC_UNIT_TYPE = "Unable to debug expression with no return value."
    }
}

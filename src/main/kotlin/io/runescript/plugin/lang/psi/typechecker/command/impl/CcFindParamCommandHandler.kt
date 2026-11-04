package io.runescript.plugin.lang.psi.typechecker.command.impl

import io.runescript.plugin.lang.psi.typechecker.TypeCheckingContext
import io.runescript.plugin.lang.psi.typechecker.command.DynamicCommandHandler
import io.runescript.plugin.lang.psi.typechecker.type
import io.runescript.plugin.lang.psi.typechecker.type.MetaType
import io.runescript.plugin.lang.psi.typechecker.type.ParamType
import io.runescript.plugin.lang.psi.typechecker.type.PrimitiveType
import io.runescript.plugin.lang.psi.typechecker.type.ScriptVarType
import io.runescript.plugin.lang.psi.typechecker.type.TupleType

/** Type-checks the three- and five-argument forms of `cc_find_param`. */
class CcFindParamCommandHandler : DynamicCommandHandler {
    override fun TypeCheckingContext.typeCheck() {
        checkArgument(0, ScriptVarType.COMPONENT)

        val firstParam = checkArgument(1, ParamCommandHandler.PARAM_ANY)
        val firstType = (firstParam?.type as? ParamType)?.inner
        checkArgument(2, firstType)

        val secondParam = checkArgument(3, ParamCommandHandler.PARAM_ANY)
        val secondType = (secondParam?.type as? ParamType)?.inner
        if (secondType != null) {
            checkArgument(4, secondType)
        }

        val expected =
            if (expression.arguments.size == 5) {
                TupleType(
                    ScriptVarType.COMPONENT,
                    ParamCommandHandler.PARAM_ANY,
                    firstType ?: MetaType.Any,
                    ParamCommandHandler.PARAM_ANY,
                    secondType ?: MetaType.Any,
                )
            } else {
                TupleType(
                    ScriptVarType.COMPONENT,
                    ParamCommandHandler.PARAM_ANY,
                    firstType ?: MetaType.Any,
                )
            }

        checkArgumentTypes(expected)
        expression.type = PrimitiveType.BOOLEAN
    }
}

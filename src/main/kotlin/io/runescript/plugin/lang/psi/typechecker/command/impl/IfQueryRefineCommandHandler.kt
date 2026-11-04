package io.runescript.plugin.lang.psi.typechecker.command.impl

import io.runescript.plugin.lang.psi.typechecker.TypeCheckingContext
import io.runescript.plugin.lang.psi.typechecker.command.DynamicCommandHandler
import io.runescript.plugin.lang.psi.typechecker.type
import io.runescript.plugin.lang.psi.typechecker.type.MetaType
import io.runescript.plugin.lang.psi.typechecker.type.ParamType
import io.runescript.plugin.lang.psi.typechecker.type.PrimitiveType
import io.runescript.plugin.lang.psi.typechecker.type.TupleType

/** Type-checks `if_query_refine(param, value)` using the parameter's value type. */
class IfQueryRefineCommandHandler : DynamicCommandHandler {
    override fun TypeCheckingContext.typeCheck() {
        val param = checkArgument(0, ParamCommandHandler.PARAM_ANY)
        val valueType = (param?.type as? ParamType)?.inner
        checkArgument(1, valueType)

        checkArgumentTypes(
            TupleType(
                ParamCommandHandler.PARAM_ANY,
                valueType ?: MetaType.Any,
            ),
        )
        expression.type = PrimitiveType.INT
    }
}

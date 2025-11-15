package io.runescript.plugin.lang.psi.typechecker.command.impl

import io.runescript.plugin.lang.psi.typechecker.TypeCheckingContext
import io.runescript.plugin.lang.psi.typechecker.command.DynamicCommandHandler
import io.runescript.plugin.lang.psi.typechecker.type
import io.runescript.plugin.lang.psi.typechecker.type.MetaType
import io.runescript.plugin.lang.psi.typechecker.type.ParamType
import io.runescript.plugin.lang.psi.typechecker.type.PrimitiveType
import io.runescript.plugin.lang.psi.typechecker.type.TupleType

class IfChildrenFilterCommandHandler : DynamicCommandHandler {
    override fun TypeCheckingContext.typeCheck() {
        // check param reference
        val paramExpr = checkArgument(0, ParamCommandHandler.PARAM_ANY)
        val paramReturnType = (paramExpr?.type as? ParamType)?.inner
        checkArgument(1, paramReturnType)

        val expectedTypes = TupleType(
            ParamCommandHandler.PARAM_ANY,
            paramReturnType ?: MetaType.Any,
        )
        checkArgumentTypes(expectedTypes)
        expression.type = PrimitiveType.INT
    }
}

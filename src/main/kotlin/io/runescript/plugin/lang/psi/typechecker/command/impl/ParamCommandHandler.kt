package io.runescript.plugin.lang.psi.typechecker.command.impl

import io.runescript.plugin.lang.psi.typechecker.TypeCheckingContext
import io.runescript.plugin.lang.psi.typechecker.command.DynamicCommandHandler
import io.runescript.plugin.lang.psi.typechecker.type
import io.runescript.plugin.lang.psi.typechecker.type.MetaType
import io.runescript.plugin.lang.psi.typechecker.type.ParamType
import io.runescript.plugin.lang.psi.typechecker.type.TupleType
import io.runescript.plugin.lang.psi.typechecker.type.Type

class ParamCommandHandler(private val type: Type) : DynamicCommandHandler {
    override fun TypeCheckingContext.typeCheck() {
        // check first argument is the defined type
        checkArgument(0, type)

        // check the second argument is a param reference
        val paramExpr = checkArgument(1, PARAM_ANY)
        val paramReturnType = (paramExpr?.type as? ParamType)?.inner

        // define the expected types based on what is currently known
        val expectedTypes = TupleType(
            type,
            ParamType(paramReturnType ?: MetaType.Any),
        )

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

    companion object {
        val PARAM_ANY = ParamType(MetaType.Any)
    }
}

package io.runescript.plugin.lang.psi.typechecker.command.impl

import io.runescript.plugin.lang.psi.typechecker.TypeCheckingContext
import io.runescript.plugin.lang.psi.typechecker.command.DynamicCommandHandler
import io.runescript.plugin.lang.psi.typechecker.type
import io.runescript.plugin.lang.psi.typechecker.type.*

class IfFindChildCommandHandler : DynamicCommandHandler {
    override fun TypeCheckingContext.typeCheck() {
        checkArgument(0, ScriptVarType.COMPONENT)
        val param1Expr = checkArgument(1, ParamCommandHandler.PARAM_ANY)
        val param1ReturnType = (param1Expr?.type as? ParamType)?.inner
        checkArgument(2, param1ReturnType)

        val param2Expr = checkArgument(3, ParamCommandHandler.PARAM_ANY)
        val param2ReturnType = (param2Expr?.type as? ParamType)?.inner
        if (param2ReturnType != null) {
            checkArgument(4, param2ReturnType)
        }

        val expectedTypes = if (expression.arguments.size == 5) {
            // expect (component, param<T>, T, param<U>, U)
            TupleType(
                ScriptVarType.COMPONENT,
                ParamCommandHandler.PARAM_ANY,
                param1ReturnType ?: MetaType.Any,
                ParamCommandHandler.PARAM_ANY,
                param2ReturnType ?: MetaType.Any,
            )
        } else {
            // expect (component, param<T>, T)
            TupleType(
                ScriptVarType.COMPONENT,
                ParamCommandHandler.PARAM_ANY,
                param1ReturnType ?: MetaType.Any,
            )
        }

        checkArgumentTypes(expectedTypes)
        expression.type = PrimitiveType.BOOLEAN
    }
}

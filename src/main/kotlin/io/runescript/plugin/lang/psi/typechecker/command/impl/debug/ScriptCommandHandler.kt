package io.runescript.plugin.lang.psi.typechecker.command.impl.debug


import io.runescript.plugin.lang.psi.typechecker.TypeCheckingContext
import io.runescript.plugin.lang.psi.typechecker.command.DynamicCommandHandler
import io.runescript.plugin.lang.psi.typechecker.type
import io.runescript.plugin.lang.psi.typechecker.type.MetaType
import io.runescript.plugin.lang.psi.typechecker.type.PrimitiveType

/**
 * Dynamic command handler that replaces the call with a string constant containing
 * the name of the script it is called in.
 */
class ScriptCommandHandler : DynamicCommandHandler {
    override fun TypeCheckingContext.typeCheck() {
        checkArgumentTypes(MetaType.Unit)
        expression.type = PrimitiveType.STRING
    }
}

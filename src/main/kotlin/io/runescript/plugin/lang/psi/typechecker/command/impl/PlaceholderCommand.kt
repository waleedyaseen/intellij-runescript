package io.runescript.plugin.lang.psi.typechecker.command.impl

import io.runescript.plugin.lang.psi.typechecker.TypeCheckingContext
import io.runescript.plugin.lang.psi.typechecker.command.DynamicCommandHandler
import io.runescript.plugin.lang.psi.typechecker.type
import io.runescript.plugin.lang.psi.typechecker.type.MetaType
import io.runescript.plugin.lang.psi.typechecker.type.Type

/**
 * A command that allows replacing a command call with a constant value. The
 * return type is set to [type].
 */
class PlaceholderCommand(private val type: Type, private val value: Any) : DynamicCommandHandler {
    override fun TypeCheckingContext.typeCheck() {
        checkArgumentTypes(MetaType.Unit)
        expression.type = type
    }
}

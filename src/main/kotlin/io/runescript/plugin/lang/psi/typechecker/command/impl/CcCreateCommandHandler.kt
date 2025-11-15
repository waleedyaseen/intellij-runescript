package io.runescript.plugin.lang.psi.typechecker.command.impl

import io.runescript.plugin.lang.psi.typechecker.TypeCheckingContext
import io.runescript.plugin.lang.psi.typechecker.command.DynamicCommandHandler
import io.runescript.plugin.lang.psi.typechecker.type
import io.runescript.plugin.lang.psi.typechecker.type.MetaType
import io.runescript.plugin.lang.psi.typechecker.type.PrimitiveType
import io.runescript.plugin.lang.psi.typechecker.type.ScriptVarType
import io.runescript.plugin.lang.psi.typechecker.type.TupleType

/**
 * Handles `cc_create` having an optional 4th boolean argument for OSRS >= 230.
 */
class CcCreateCommandHandler : DynamicCommandHandler {
    override fun TypeCheckingContext.typeCheck() {
        checkArgument(0, ScriptVarType.COMPONENT) // layer
        checkArgument(1, PrimitiveType.INT) // type
        checkArgument(2, PrimitiveType.INT) // subid
        val requireNew = checkArgument(3, PrimitiveType.BOOLEAN)

        val expectedTypes = mutableListOf(
            ScriptVarType.COMPONENT,
            PrimitiveType.INT,
            PrimitiveType.INT,
        )

        // if a 4th argument is supplied then we want to make sure it's a boolean
        if (requireNew != null) {
            expectedTypes += PrimitiveType.BOOLEAN
        }

        checkArgumentTypes(TupleType.fromList(expectedTypes))
        expression.type = MetaType.Unit
    }
}

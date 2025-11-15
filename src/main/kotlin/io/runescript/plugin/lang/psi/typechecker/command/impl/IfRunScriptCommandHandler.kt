package io.runescript.plugin.lang.psi.typechecker.command.impl


import io.runescript.plugin.lang.psi.RsDynamicExpression
import io.runescript.plugin.lang.psi.typechecker.TypeCheckingContext
import io.runescript.plugin.lang.psi.typechecker.command.DynamicCommandHandler
import io.runescript.plugin.lang.psi.typechecker.type
import io.runescript.plugin.lang.psi.typechecker.type.*

/**
 * Handles the `if_runscript*` command. This command accepts a secondary set of arguments
 * that are forwarded to the server and used within the invoked script.
 *
 * The component argument must be known at compile time. The script lookup is performed by
 * looking up a [IfScriptType], which defines the expected argument types required to invoke
 * the target script.
 *
 * Example:
 * ```
 * if_runscript*(some_script, inter:comp, null)(true)
 * ```
 */
class IfRunScriptCommandHandler : DynamicCommandHandler {
    override fun TypeCheckingContext.typeCheck() {
        val ifScript = checkArgument(0, IF_SCRIPT_ANY)
        val com = checkArgument(1, ScriptVarType.COMPONENT)
        checkArgument(2, PrimitiveType.INT)

        val expectedTypesList = arrayListOf(
            IF_SCRIPT_ANY,
            ScriptVarType.COMPONENT,
            PrimitiveType.INT,
        )

        if (checkArgumentTypes(TupleType.fromList(expectedTypesList))) {
            // button shouldn't be null here since we're within the block that checks the expected types
            // which requires at least 3 arguments.
            if (com != null && com !is RsDynamicExpression) {
                // the semantics of this command requires the component to be a static reference to
                // look up the proper script. it isn't technically necessary currently, but we still
                // check for it to make migration easier later on.
                // Note: it is currently unknown exactly how the component is involved with the resolution.
                com.reportError(diagnostics, "Component reference must be constant.")
            }
        }

        val ifScriptExpressionType = ifScript?.type
        if (ifScriptExpressionType is IfScriptType) {
            val types = TupleType.toList(ifScriptExpressionType.inner)
            for ((i, type) in types.withIndex()) {
                checkArgument(i, type, args2 = true)
            }
            checkArgumentTypes(ifScriptExpressionType.inner, args2 = true)
        }

        expression.type = MetaType.Unit
    }

    private companion object {
        val IF_SCRIPT_ANY = IfScriptType(MetaType.Any)
    }
}

package io.runescript.plugin.lang.psi.typechecker.command.impl

import io.runescript.plugin.lang.psi.typechecker.TypeCheckingContext
import io.runescript.plugin.lang.psi.typechecker.command.DynamicCommandHandler
import io.runescript.plugin.lang.psi.typechecker.type
import io.runescript.plugin.lang.psi.typechecker.type.DbColumnType
import io.runescript.plugin.lang.psi.typechecker.type.MetaType
import io.runescript.plugin.lang.psi.typechecker.type.PrimitiveType
import io.runescript.plugin.lang.psi.typechecker.type.TupleType

class DbFindCommandHandler(private val withCount: Boolean) : DynamicCommandHandler {
    override fun TypeCheckingContext.typeCheck() {
        // lookup the column expression
        val columnExpr = checkArgument(0, DbColumnType(MetaType.Any))

        // typehint the second argument using the dbcolumn type if it was valid
        val keyType = (columnExpr?.type as? DbColumnType)?.inner
        checkArgument(1, keyType)

        // define the expected types based on what is currently known
        val expectedTypes = TupleType(
            DbColumnType(keyType ?: MetaType.Any),
            keyType ?: MetaType.Any,
        )

        // check that the key type is not a tuple type
        if (keyType is TupleType) {
            columnExpr.reportError(diagnostics, "Tuple columns are not supported.")
        } else {
            // compare the expected types with the actual types
            checkArgumentTypes(expectedTypes)
        }

        // set the return type
        expression.type = if (withCount) PrimitiveType.INT else MetaType.Unit
    }
}

package io.runescript.plugin.lang.psi.typechecker.command.impl.array

import io.runescript.plugin.lang.psi.typechecker.TypeCheckingContext
import io.runescript.plugin.lang.psi.typechecker.command.DynamicCommandHandler
import io.runescript.plugin.lang.psi.typechecker.type
import io.runescript.plugin.lang.psi.typechecker.type.MetaType
import io.runescript.plugin.lang.psi.typechecker.type.PrimitiveType
import io.runescript.plugin.lang.psi.typechecker.type.TupleType
import io.runescript.plugin.lang.psi.typechecker.type.wrapped.ArrayType

/**
 * An implementation of [DynamicCommandHandler] that adds support for type checking
 * the `array_delete` command within scripts.
 *
 * Example:
 * ```
 * def_int $deleted = array_delete($intarray, 0);
 * ```
 */
class ArrayDeleteCommandHandler : DynamicCommandHandler {
    override fun TypeCheckingContext.typeCheck() {
        val array1Expr = checkArgument(0, ArrayType(MetaType.Any))
        val array1ExprType = array1Expr?.type
        checkArgument(1, PrimitiveType.INT)

        // check the base signature matches
        if (!checkArgumentTypes(BASE_EXPECTED_TYPES) || array1ExprType !is ArrayType) {
            expression.type = MetaType.Error
            return
        }

        expression.type = array1ExprType.inner
    }

    private companion object {
        val BASE_EXPECTED_TYPES = TupleType(
            ArrayType(MetaType.Any),
            PrimitiveType.INT,
        )
    }
}

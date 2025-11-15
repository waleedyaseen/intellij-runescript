package io.runescript.plugin.lang.psi.typechecker.command.impl.array

import io.runescript.plugin.lang.psi.typechecker.TypeCheckingContext
import io.runescript.plugin.lang.psi.typechecker.command.DynamicCommandHandler
import io.runescript.plugin.lang.psi.typechecker.type
import io.runescript.plugin.lang.psi.typechecker.type.MetaType
import io.runescript.plugin.lang.psi.typechecker.type.TupleType
import io.runescript.plugin.lang.psi.typechecker.type.wrapped.ArrayType

/**
 * An implementation of [DynamicCommandHandler] that adds support for type checking
 * the `array_pushall` command within scripts.
 *
 * Example:
 * ```
 * array_pushall($array1, $array2)
 * ```
 */
class ArrayPushallCommandHandler : DynamicCommandHandler {
    override fun TypeCheckingContext.typeCheck() {
        val array1Expr = checkArgument(0, ArrayType(MetaType.Any))
        val array1ExprType = array1Expr?.type
        checkArgument(1, array1ExprType)

        // check the base signature matches
        if (checkArgumentTypes(BASE_EXPECTED_TYPES) && array1ExprType is ArrayType) {
            val expectedTypes = TupleType(
                array1ExprType,
                array1ExprType,
            )
            checkArgumentTypes(expectedTypes)
        }

        expression.type = MetaType.Unit
    }

    private companion object {
        val BASE_EXPECTED_TYPES = TupleType(
            ArrayType(MetaType.Any),
            ArrayType(MetaType.Any),
        )
    }
}

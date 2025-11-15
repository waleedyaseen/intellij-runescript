package io.runescript.plugin.lang.psi.typechecker.command.impl.array



import io.runescript.plugin.lang.psi.typechecker.TypeCheckingContext
import io.runescript.plugin.lang.psi.typechecker.command.DynamicCommandHandler
import io.runescript.plugin.lang.psi.typechecker.type
import io.runescript.plugin.lang.psi.typechecker.type.MetaType
import io.runescript.plugin.lang.psi.typechecker.type.TupleType
import io.runescript.plugin.lang.psi.typechecker.type.wrapped.ArrayType

/**
 * An implementation of [DynamicCommandHandler] that adds support for type checking
 * the `array_push` command within scripts.
 *
 * Example:
 * ```
 * array_push($intarray, 123)
 * ```
 */
class ArrayPushCommandHandler : DynamicCommandHandler {
    override fun TypeCheckingContext.typeCheck() {
        val arrayExpr = checkArgument(0, ArrayType(MetaType.Any))
        val arrayExprType = arrayExpr?.type
        val arrayExprInnerType = (arrayExprType as? ArrayType)?.inner
        checkArgument(1, arrayExprInnerType)

        // check the base signature matches
        if (checkArgumentTypes(BASE_EXPECTED_TYPES) && arrayExprType is ArrayType) {
            // expect (array<T>, T)
            val expectedTypes = TupleType(arrayExprType, arrayExprType.inner)
            checkArgumentTypes(expectedTypes)
        }

        expression.type = MetaType.Unit
    }

    private companion object {
        val BASE_EXPECTED_TYPES = TupleType(
            ArrayType(MetaType.Any),
            MetaType.Any,
        )
    }
}

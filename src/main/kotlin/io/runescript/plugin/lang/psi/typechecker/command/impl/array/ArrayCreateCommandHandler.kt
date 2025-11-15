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
 * the `array_create` command within scripts.
 *
 * Example:
 * ```
 * array_create(int, 0, 10)
 * ```
 */
class ArrayCreateCommandHandler : DynamicCommandHandler {
    override fun TypeCheckingContext.typeCheck() {
        val typeExpr = checkTypeArgument(0)
        val type = typeExpr?.type as? MetaType.Type
        checkArgument(1, PrimitiveType.INT)
        checkArgument(2, PrimitiveType.INT)

        // check the base signature matches
        if (!checkArgumentTypes(BASE_EXPECTED_TYPES) || type == null) {
            expression.type = MetaType.Error
            return
        }

        expression.type = ArrayType(type.inner)
    }

    private companion object {
        val BASE_EXPECTED_TYPES = TupleType(
            MetaType.Type(MetaType.Any),
            PrimitiveType.INT,
            PrimitiveType.INT,
        )
    }
}

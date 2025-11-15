package io.runescript.plugin.lang.psi.typechecker.type.wrapped

import io.runescript.plugin.lang.psi.typechecker.type.Type

/**
 * A type that contains an inner type. This is intended for more complex types
 * that resolve to a different type depending on how they're accessed. This is
 * necessary for some cases where you want to verify a reference is of a type
 * without seeing what the "execution" type would be.
 *
 * @see ArrayType
 */
interface WrappedType : Type {
    /**
     * The inner type that is being wrapped.
     */
    val inner: Type
}

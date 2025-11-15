package io.runescript.plugin.lang.psi.typechecker.type

/**
 * Represents a type that we use in the type system to make sure everything is only assigned the correct thing.
 *
 * @see PrimitiveType
 * @see TupleType
 */
interface Type {
    /**
     * A string used to represent the type. This is what is used in scripts to reference it. E.g. `def_int` or `int`
     * would rely on there being a type with a representation of `int`.
     */
    val representation: String

    /**
     * The character representation of the type.
     */
    val code: Char?

    /**
     * The base type of the type. This type determines which stack the type uses.
     */
    val baseType: BaseVarType?

    /**
     * The default value of the type.
     */
    val defaultValue: Any?

    /**
     * Options the type allows or disallows.
     */
    val options: TypeOptions
        get() = MutableTypeOptions()
}

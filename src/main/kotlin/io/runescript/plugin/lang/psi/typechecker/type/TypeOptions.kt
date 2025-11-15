package io.runescript.plugin.lang.psi.typechecker.type

/**
 * Defines options to enable or disable features for a specific [Type].
 */
sealed interface TypeOptions {
    /**
     * Whether the type is allowed to be used within a switch statement.
     *
     * Default: `true`
     */
    val allowSwitch: Boolean

    /**
     * Whether the type is allowed to be used in an array.
     *
     * Default: `true`
     */
    val allowArray: Boolean

    /**
     * Whether the type is allowed to be declared as a parameter or local variable.
     *
     * Default: `true`
     */
    val allowDeclaration: Boolean
}

/**
 * Implementation of [TypeOptions] with the properties mutable.
 */
data class MutableTypeOptions(
    override var allowSwitch: Boolean = true,
    override var allowArray: Boolean = true,
    override var allowDeclaration: Boolean = true,
) : TypeOptions

package io.runescript.plugin.lang.psi.typechecker.trigger

import io.runescript.plugin.lang.psi.typechecker.type.Type as ScriptType

/**
 * Determines how a script subject is validated.
 */
sealed interface SubjectMode {
    /**
     * A subject mode that only allows global (`_`) scripts.
     */
    data object None : SubjectMode

    /**
     * A subject mode specifies the subject as just part of the script name and is
     * not a reference to a symbol.
     */
    data object Name : SubjectMode

    /**
     * A subject mode that specifies the subject is a `Type` of some sort.
     */
    data class Type(
        val type: ScriptType,
        val category: Boolean = true,
        val global: Boolean = true,
    ) : SubjectMode
}

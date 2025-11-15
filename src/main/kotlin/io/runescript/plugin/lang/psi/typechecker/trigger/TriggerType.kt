package io.runescript.plugin.lang.psi.typechecker.trigger

import io.runescript.plugin.lang.psi.typechecker.type.Type

/**
 * A trigger type of a script. The trigger type is the first part of a script declaration (`[trigger,_]`) where
 * each trigger has different functionality and uses.
 */
interface TriggerType {
    /**
     * A unique number to identify the trigger.
     */
    val id: Int

    /**
     * The text that represents the trigger. This is the string that is used to identifier the trigger when defining a
     * script.
     *
     * ```
     * [<identifier>,<subject>]
     * ```
     */
    val identifier: String

    /**
     * The [SubjectMode] for the trigger.
     */
    val subjectMode: SubjectMode

    /**
     * Whether parameters are allowed in scripts using the trigger.
     */
    val allowParameters: Boolean

    /**
     * The parameters that must be defined. If `null` no arguments are expected.
     */
    val parameters: Type?

    /**
     * Whether returns are allowed in scripts using the trigger.
     */
    val allowReturns: Boolean

    /**
     * The return types that must be defined. If `null` no returns are expected.
     */
    val returns: Type?
}

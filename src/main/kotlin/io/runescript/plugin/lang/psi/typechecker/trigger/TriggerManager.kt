package io.runescript.plugin.lang.psi.typechecker.trigger

import kotlin.reflect.KClass

/**
 * Handles mapping from name to [TriggerType].
 */
class TriggerManager {
    /**
     * A map of trigger names to the [TriggerType].
     */
    private val nameToTrigger = mutableMapOf<String, TriggerType>()

    /**
     * Registers [trigger] using [name] for lookup.
     */
    fun register(name: String, trigger: TriggerType) {
        val existingTrigger = nameToTrigger.putIfAbsent(name, trigger)
        if (existingTrigger != null) {
            error("Trigger '$name' is already registered.")
        }
    }

    /**
     * Registers [trigger] using [TriggerType.identifier] for lookup.
     */
    fun register(trigger: TriggerType) {
        register(trigger.identifier, trigger)
    }

    /**
     * Registers all values within [enum] to the name lookup.
     */
    fun <T> registerAll(enum: KClass<T>) where T : Enum<T>, T : TriggerType {
        for (value in enum.java.enumConstants) {
            register(value)
        }
    }

    /**
     * Registers all values within [T] to the name lookup.
     */
    inline fun <reified T> registerAll() where T : Enum<T>, T : TriggerType {
        registerAll(T::class)
    }

    /**
     * Finds a trigger by [name]. If a trigger was not found an error is thrown.
     */
    fun find(name: String): TriggerType = nameToTrigger[name] ?: error("Unable to find trigger '$name'.")

    /**
     * Finds a trigger by [name].
     *
     * If a trigger with the name was not found, `null` is returned.
     */
    fun findOrNull(name: String): TriggerType? = nameToTrigger[name]
}

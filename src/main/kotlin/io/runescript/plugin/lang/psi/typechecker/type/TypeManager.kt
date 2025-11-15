package io.runescript.plugin.lang.psi.typechecker.type

import io.runescript.plugin.lang.psi.typechecker.type.wrapped.ArrayType
import org.mozilla.javascript.Token.typeToName
import kotlin.reflect.KClass

typealias TypeChecker = (left: Type, right: Type) -> Boolean
typealias TypeBuilder = MutableTypeOptions.() -> Unit

/**
 * Handles the mapping from name to [Type] along with centralized location for comparing types.
 */
class TypeManager {
    /**
     * A map of type names to the [Type].
     */
    private val nameToType = mutableMapOf<String, Type>()

    /**
     * A map of [Type] to the type name.
     */
    private val typeToName = mutableMapOf<Type, String>()

    /**
     * A list of possible checkers to run against types.
     */
    private val checkers = mutableListOf<TypeChecker>()

    private var cachedTypeKeywords = hashSetOf<CharSequence>()
    private var cachedDefineKeywords = hashSetOf<CharSequence>()
    private var cachedSwitchKeywords = hashSetOf<CharSequence>()
    private var cacheDirty = true

    val typeKeywords: Set<CharSequence>
        get() {
            checkCache()
            return cachedTypeKeywords
        }

    val defineKeywords: Set<CharSequence>
        get() {
            checkCache()
            return cachedDefineKeywords
        }

    val switchKeywords: Set<CharSequence>
        get() {
            checkCache()
            return cachedSwitchKeywords
        }

    /**
     * Registers [type] using [name] for lookup.
     */
    fun register(name: String, type: Type) {
        val existingType = nameToType.putIfAbsent(name, type)
        if (existingType != null) {
            error("Type '$name' is already registered.")
        }
        typeToName[type] = name
        cacheDirty = true
    }

    /**
     * Registers [type] using [Type.representation] for lookup.
     */
    fun register(type: Type) {
        register(type.representation, type)
    }

    /**
     * Creates and registers a new type.
     */
    fun register(
        name: String,
        code: Char? = null,
        baseType: BaseVarType = BaseVarType.INTEGER,
        defaultValue: Any = -1,
        builder: TypeBuilder? = null,
    ): Type {
        val options = MutableTypeOptions()
        builder?.invoke(options)

        val newType = object : Type {
            override val representation = name
            override val code = code
            override val baseType = baseType
            override val defaultValue = defaultValue
            override val options = options
        }
        register(newType)
        return newType
    }

    /**
     * Registers all values within [enum] to the name lookup.
     */
    fun <T> registerAll(enum: KClass<T>) where T : Enum<T>, T : Type {
        for (value in enum.java.enumConstants) {
            register(value)
        }
    }

    /**
     * Registers all values within [T] to the name lookup.
     */
    inline fun <reified T> registerAll() where T : Enum<T>, T : Type {
        registerAll(T::class)
    }

    /**
     * Searches for [name] and allows changing the [me.filby.neptune.runescript.compiler.type.TypeOptions] for the type. If a
     * type wasn't found with the given name an error is thrown.
     */
    fun changeOptions(name: String, builder: TypeBuilder) {
        val type = nameToType[name] ?: error("$name was not found")
        val options = type.options as MutableTypeOptions
        options.builder()
        cacheDirty = true
    }

    /**
     * Finds a type by [name]. If [allowArray] is enabled, names ending with `array`
     * will attempt to find and wrap the type with [ArrayType].
     *
     * If the type doesn't exist an exception is thrown.
     */
    fun find(name: String, allowArray: Boolean = false): Type =
        findOrNull(name, allowArray) ?: error("Unable to find type: '$name'")

    /**
     * Finds a type by [name]. If [allowArray] is enabled, names ending with `array`
     * will attempt to find and wrap the type with [ArrayType].
     *
     * If the type doesn't exist, `null` is returned.
     */
    fun findOrNull(name: String, allowArray: Boolean = false): Type? {
        if (allowArray && name.length > ARRAY_SUFFIX_LENGTH && name.endsWith(ARRAY_SUFFIX)) {
            // substring before the last "array" to prevent requesting intarrayarray (or deeper)
            val baseType = name.substringBeforeLast(ARRAY_SUFFIX)
            val type = findOrNull(baseType)
            if (type == null || !type.options.allowArray) {
                return null
            }
            return ArrayType(type)
        }
        return nameToType[name]
    }

    /**
     * Adds [checker] to be called when calling [check].
     *
     * A checker should aim to only do simple checks and avoid covering a wide range of
     * types unless you really know what you're doing.
     *
     * The follow example would allow `namedobj` to be assigned to `obj` but not vice-versa.
     * ```
     * addTypeChecker { left, right -> left == OBJ && right == NAMEDOBJ }
     * ```
     */
    fun addTypeChecker(checker: TypeChecker) {
        checkers += checker
    }

    /**
     * Checks to see if [right] is assignable to [left].
     */
    fun check(left: Type, right: Type): Boolean = checkers.any { checker -> checker(left, right) }

    fun checkCache() {
        if (!cacheDirty) return
        cacheDirty = false

        cachedTypeKeywords.clear()
        cachedTypeKeywords.addAll(generateTypeKeyword())

        cachedDefineKeywords.clear()
        cachedDefineKeywords.addAll(generateDefineKeywords())

        cachedSwitchKeywords.clear()
        cachedSwitchKeywords.addAll(generateSwitchKeyword())
    }


    private fun generateDefineKeywords(): List<CharSequence> {
        val keywords = mutableListOf<CharSequence>()
        for ((name, type) in nameToType) {
            if (type.options.allowDeclaration) {
                keywords.add("def_$name")
                if (type.options.allowArray) {
                    keywords.add("def_${name}array")
                }
            }
        }
        return keywords
    }

    private fun generateTypeKeyword(): List<CharSequence> {
        val keywords = mutableListOf<CharSequence>()
        for ((name, type) in nameToType) {
            keywords.add(name)
            if (type.options.allowArray) {
                keywords.add("${name}array")
            }
        }
        return keywords
    }

    private fun generateSwitchKeyword(): List<CharSequence> {
        val keywords = mutableListOf<CharSequence>()
        for ((name, type) in nameToType) {
            if (type.options.allowSwitch) {
                keywords.add("switch_$name")
            }
        }
        return keywords
    }

    fun findName(type: Type) = typeToName[type]

    private companion object {
        private const val ARRAY_SUFFIX = "array"
        private const val ARRAY_SUFFIX_LENGTH = ARRAY_SUFFIX.length
    }
}

package io.runescript.plugin.ide.parameter

enum class RsParameterBehaviorOptionKind {
    NONE,
    CONSTANTS,
}

data class RsParameterBehaviorDefinition(
    val id: String,
    val requiredTypeName: String? = null,
    val optionKind: RsParameterBehaviorOptionKind = RsParameterBehaviorOptionKind.NONE,
)

object RsParameterBehaviorRegistry {
    const val RGB_ID = "rgb"
    const val ARGB_ID = "argb"
    const val CONSTANT_ID = "constant"

    val definitions: List<RsParameterBehaviorDefinition> =
        listOf(
            RsParameterBehaviorDefinition(RGB_ID, requiredTypeName = "int"),
            RsParameterBehaviorDefinition(ARGB_ID, requiredTypeName = "int"),
            RsParameterBehaviorDefinition(CONSTANT_ID, optionKind = RsParameterBehaviorOptionKind.CONSTANTS),
        )

    private val definitionsById = definitions.associateBy(RsParameterBehaviorDefinition::id)

    fun find(id: String): RsParameterBehaviorDefinition? = definitionsById[id]
}

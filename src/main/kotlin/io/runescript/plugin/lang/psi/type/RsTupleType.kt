package io.runescript.plugin.lang.psi.type

data class RsTupleType(val types: Array<RsType>) : RsType {

    override val representation: String
        get() = types.joinToString(",") { it.representation }

    override val size: Int
        get() = types.size

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RsTupleType

        return types.contentEquals(other.types)
    }

    override fun hashCode(): Int {
        return types.contentHashCode()
    }

}

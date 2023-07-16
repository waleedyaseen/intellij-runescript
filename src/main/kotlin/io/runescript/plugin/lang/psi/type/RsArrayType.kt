package io.runescript.plugin.lang.psi.type

data class RsArrayType(val elementType: RsType) : RsType {

    override val representation: String
        get() = "${elementType.representation}array"

    override val size: Int
        get() = 1
}
package io.runescript.plugin.lang.psi.type

object RsUnitType : RsType {
    override val representation: String
        get() = "<unit>"
    override val size: Int
        get() = 1
}
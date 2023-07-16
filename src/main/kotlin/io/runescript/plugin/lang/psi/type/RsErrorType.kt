package io.runescript.plugin.lang.psi.type

object RsErrorType : RsType {
    override val representation: String
        get() = "<type error>"
    override val size: Int
        get() = 1
}
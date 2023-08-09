package io.runescript.plugin.lang.psi.type

interface RsType {

    val representation: String
    val size: Int
        get() = 1
}

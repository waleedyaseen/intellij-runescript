package io.runescript.plugin.lang.psi

object RuneScriptTokenTypes {
    @JvmField
    val SINGLE_LINE_COMMENT = RuneScriptElementType("SINGLE_LINE_COMMENT")
    @JvmField
    val MULTI_LINE_COMMENT = RuneScriptElementType("MULTI_LINE_COMMENT")
    @JvmField
    val STRING_LITERAL = RuneScriptElementType("STRING_LITERAL")
}
package io.runescript.plugin.symbollang

import com.intellij.lang.Language

object RuneScriptSymbol : Language("RuneScriptSymbol") {
    private fun readResolve(): Any = RuneScriptSymbol
}
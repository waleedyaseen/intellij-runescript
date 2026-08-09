package io.runescript.plugin.lang

import com.intellij.lang.Language

object RuneScriptHook : Language(RuneScript, "RuneScriptHook") {
    private fun readResolve(): Any = RuneScriptHook
}

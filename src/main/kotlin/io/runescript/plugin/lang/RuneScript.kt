package io.runescript.plugin.lang

import com.intellij.lang.Language

object RuneScript : Language("RuneScript") {
    private fun readResolve(): Any = RuneScript
}
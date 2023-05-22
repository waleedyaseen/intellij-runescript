package io.runescript.plugin.ide.formatter.impl

import com.intellij.formatting.SpacingBuilder
import com.intellij.psi.codeStyle.CodeStyleSettings
import io.runescript.plugin.lang.RuneScript
import io.runescript.plugin.lang.psi.RsElementTypes.*

fun createSpacingBuilder(settings: CodeStyleSettings): SpacingBuilder {
    return SpacingBuilder(settings, RuneScript)
            .after(SCRIPT_HEADER).lineBreakInCode()
            .afterInside(COLON, SWITCH_CASE).lineBreakInCode()
            .afterInside(COMMA, SWITCH_CASE).spaces(1)
}
package io.runescript.plugin.ide.formatter.impl

import com.intellij.formatting.SpacingBuilder
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.tree.TokenSet
import io.runescript.plugin.ide.formatter.style.RsCodeStyleSettings
import io.runescript.plugin.lang.RuneScript
import io.runescript.plugin.lang.psi.RsElementTypes.*

fun createSpacingBuilder(settings: CodeStyleSettings): SpacingBuilder {
    val rscs = settings.getCustomSettings(RsCodeStyleSettings::class.java)
    val cs = settings.getCommonSettings(RuneScript)
    return SpacingBuilder(settings, RuneScript)
            .after(SCRIPT_HEADER).lineBreakInCode()
            .afterInside(COLON, SWITCH_CASE).lineBreakInCode()
            .beforeInside(COLON, SWITCH_CASE).spacing(1, 1, 0, false, 0)
            .afterInside(COMMA, SWITCH_CASE).spacing(1, 1, 0, false, 0)
            .after(SCRIPT).blankLines(1)
}
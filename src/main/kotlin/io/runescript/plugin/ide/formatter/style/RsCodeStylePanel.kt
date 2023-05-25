package io.runescript.plugin.ide.formatter.style

import com.intellij.application.options.TabbedLanguageCodeStylePanel
import com.intellij.psi.codeStyle.CodeStyleSettings
import io.runescript.plugin.lang.RuneScript

class RsCodeStylePanel(currentSettings: CodeStyleSettings?, settings: CodeStyleSettings) : TabbedLanguageCodeStylePanel(RuneScript, currentSettings, settings)

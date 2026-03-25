package io.runescript.plugin.ide.formatter.style

import com.intellij.application.options.CodeStyleAbstractConfigurable
import com.intellij.application.options.CodeStyleAbstractPanel
import com.intellij.psi.codeStyle.CodeStyleConfigurable
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider
import com.intellij.psi.codeStyle.CustomCodeStyleSettings
import io.runescript.plugin.lang.RuneScript

class RsCodeStyleSettingsProvider : CodeStyleSettingsProvider() {
    override fun getLanguage() = RuneScript

    override fun getConfigurableId() = "preferences.sourceCode.RuneScript"

    override fun createConfigurable(
        settings: CodeStyleSettings,
        modelSettings: CodeStyleSettings,
    ): CodeStyleConfigurable =
        object : CodeStyleAbstractConfigurable(settings, modelSettings, configurableDisplayName) {
            override fun createPanel(settings: CodeStyleSettings): CodeStyleAbstractPanel = RsCodeStylePanel(currentSettings, settings)
        }

    override fun createCustomSettings(settings: CodeStyleSettings): CustomCodeStyleSettings = RsCodeStyleSettings(settings)

    override fun getConfigurableDisplayName() = "RuneScript"
}

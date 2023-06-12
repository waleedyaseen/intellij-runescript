package io.runescript.plugin.ide.execution.run

import com.intellij.execution.configurations.ConfigurationTypeBase
import io.runescript.plugin.ide.RsIcons


class RsRunConfigurationType : ConfigurationTypeBase(ID,
    "RuneScript Build",
    "Run RuneScript build",
    RsIcons.RuneScript) {

    init {
        addFactory(RsRunConfigurationFactory(this))
    }

    companion object {
        const val ID = "RuneScriptRun"
    }
}
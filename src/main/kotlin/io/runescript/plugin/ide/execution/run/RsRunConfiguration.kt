package io.runescript.plugin.ide.execution.run

import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunConfigurationBase
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project

class RsRunConfiguration(project: Project, factory: ConfigurationFactory?, name: String?)
    : RunConfigurationBase<RsRunConfigurationOptions>(project, factory, name) {

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState {
        return RsRunConfigurationRunState()
    }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> {
        return RsRunConfigurationEditor()
    }
}
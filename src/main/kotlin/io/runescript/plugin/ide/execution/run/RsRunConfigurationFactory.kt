package io.runescript.plugin.ide.execution.run

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.Project

class RsRunConfigurationFactory(type: RsRunConfigurationType) : ConfigurationFactory(type) {

    override fun getId() = RsRunConfigurationType.ID

    override fun createTemplateConfiguration(project: Project): RunConfiguration {
        return RsRunConfiguration(project, this, type.configurationTypeDescription)
    }

    override fun getOptionsClass() = RsRunConfigurationOptions::class.java
}
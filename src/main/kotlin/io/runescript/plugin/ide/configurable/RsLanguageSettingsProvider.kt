package io.runescript.plugin.ide.configurable

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurableProvider
import com.intellij.openapi.project.Project

class RsLanguageSettingsProvider(private val project: Project) : ConfigurableProvider() {

    override fun createConfigurable(): Configurable? {
        return RsLanguageSettings(project)
    }

    override fun canCreateConfigurable(): Boolean {
        return true // TODO(Walied): Check if this works
//        return project.modules.any { ModuleType.get(it) is RsModuleType }
    }
}
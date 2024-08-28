package io.runescript.plugin.ide.neptune

import com.intellij.openapi.externalSystem.service.settings.AbstractExternalSystemConfigurable
import com.intellij.openapi.externalSystem.util.ExternalSystemSettingsControl
import com.intellij.openapi.project.Project

class NeptuneSystemConfigurable(project: Project) :
    AbstractExternalSystemConfigurable<NeptuneProjectSettings, NeptuneSettingsListener, NeptuneSettings>(
        project, Neptune.SYSTEM_ID
    ) {

    override fun getId() = "neptune.project.settings.configurable"

    override fun newProjectSettings(): NeptuneProjectSettings {
        return NeptuneProjectSettings()
    }

    override fun createSystemSettingsControl(settings: NeptuneSettings): ExternalSystemSettingsControl<NeptuneSettings>? {
        return NeptuneSystemSettingsControl(settings)
    }

    override fun createProjectSettingsControl(settings: NeptuneProjectSettings): ExternalSystemSettingsControl<NeptuneProjectSettings> {
        return NeptuneProjectSettingsControl(settings)
    }
}
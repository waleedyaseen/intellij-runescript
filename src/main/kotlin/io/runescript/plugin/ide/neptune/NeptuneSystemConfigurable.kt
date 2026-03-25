package io.runescript.plugin.ide.neptune

import com.intellij.openapi.externalSystem.service.settings.AbstractExternalSystemConfigurable
import com.intellij.openapi.externalSystem.util.ExternalSystemSettingsControl
import com.intellij.openapi.project.Project

class NeptuneSystemConfigurable(
    project: Project,
) : AbstractExternalSystemConfigurable<NeptuneProjectSettings, NeptuneSettingsListener, NeptuneSettings>(
        project,
        Neptune.SYSTEM_ID,
    ) {
    override fun getId() = "neptune.project.settings.configurable"

    override fun newProjectSettings(): NeptuneProjectSettings = NeptuneProjectSettings()

    override fun createSystemSettingsControl(settings: NeptuneSettings): ExternalSystemSettingsControl<NeptuneSettings>? =
        NeptuneSystemSettingsControl(settings)

    override fun createProjectSettingsControl(settings: NeptuneProjectSettings): ExternalSystemSettingsControl<NeptuneProjectSettings> =
        NeptuneProjectSettingsControl(settings)
}

package io.runescript.plugin.ide.neptune

import com.intellij.openapi.externalSystem.service.settings.AbstractImportFromExternalSystemControl
import com.intellij.openapi.project.ProjectManager

class NeptuneImportControl :
    AbstractImportFromExternalSystemControl<NeptuneProjectSettings, NeptuneSettingsListener, NeptuneSettings>(
        Neptune.SYSTEM_ID,
        NeptuneSettings(ProjectManager.getInstance().defaultProject),
        NeptuneProjectSettings(),
        true
    ) {

    override fun onLinkedProjectPathChange(p0: String) {
    }

    override fun createSystemSettingsControl(settings: NeptuneSettings): NeptuneSystemSettingsControl {
        return NeptuneSystemSettingsControl(settings)
    }

    override fun createProjectSettingsControl(settings: NeptuneProjectSettings): NeptuneProjectSettingsControl {
        return NeptuneProjectSettingsControl(settings)
    }

}
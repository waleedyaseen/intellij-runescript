package io.runescript.plugin.ide.neptune

import com.intellij.openapi.externalSystem.service.settings.AbstractExternalProjectSettingsControl
import com.intellij.openapi.externalSystem.util.PaintAwarePanel

class NeptuneProjectSettingsControl(settings: NeptuneProjectSettings) :
    AbstractExternalProjectSettingsControl<NeptuneProjectSettings>(settings) {

    override fun validate(settings: NeptuneProjectSettings): Boolean {
        return true
    }

    override fun fillExtraControls(content: PaintAwarePanel, indentLevel: Int) {
    }

    override fun isExtraSettingModified(): Boolean {
        return false
    }

    override fun resetExtraSettings(isDefaultModuleCreation: Boolean) {
    }

    override fun applyExtraSettings(settings: NeptuneProjectSettings) {
    }
}
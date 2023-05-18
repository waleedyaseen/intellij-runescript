package io.runescript.plugin.ide.projectWizard

import com.intellij.ide.util.projectWizard.SettingsStep
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.platform.ProjectGeneratorPeer
import javax.swing.JComponent
import javax.swing.JPanel

class RsGeneratorPeer : ProjectGeneratorPeer<RsProjectWizardData> {

    override fun getComponent(): JComponent {
        return JPanel()
    }

    override fun buildUI(settingsStep: SettingsStep) {

    }

    override fun getSettings(): RsProjectWizardData {
        return RsProjectWizardData()
    }

    override fun validate(): ValidationInfo? {
        return null
    }

    override fun isBackgroundJobRunning(): Boolean {
        return false
    }
}
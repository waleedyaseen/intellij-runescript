package io.runescript.plugin.ide.projectWizard

import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import javax.swing.JComponent

class RsModuleWizardStep(private val wizardStep: WizardContext)  : ModuleWizardStep() {

    private val peer =  RsGeneratorPeer()

    override fun getComponent(): JComponent {
        return peer.component
    }

    override fun updateDataModel() {

    }

}
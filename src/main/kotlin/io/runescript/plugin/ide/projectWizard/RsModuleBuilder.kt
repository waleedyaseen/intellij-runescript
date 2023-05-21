package io.runescript.plugin.ide.projectWizard

import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.Disposable
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.roots.ModifiableRootModel
import io.runescript.plugin.ide.RsBundle
import javax.swing.Icon

class RsModuleBuilder : ModuleBuilder() {

    override fun getModuleType() = RsModuleType()
    override fun getBuilderId() = "runescript"
    override fun getWeight() = 5000
    override fun getPresentableName() = RsBundle.message("module.builder.name")
    override fun getCustomOptionsStep(context: WizardContext, parentDisposable: Disposable?) = RsModuleWizardStep(context)

    override fun setupRootModel(modifiableRootModel: ModifiableRootModel) {
        val contentEntry = doAddContentEntry(modifiableRootModel)
        val projectDir = contentEntry?.file ?: return
        val module = modifiableRootModel.module
        RsProjectTemplate().generateTemplate(module, contentEntry, projectDir)
    }
}
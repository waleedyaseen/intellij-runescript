package io.runescript.plugin.ide.projectWizard

import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.ide.wizard.withVisualPadding
import com.intellij.openapi.Disposable
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.observable.properties.GraphProperty
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.openapi.projectRoots.SdkTypeId
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.ide.sdk.RsSdkType
import java.util.function.Supplier
import javax.swing.JComponent

class RsModuleBuilder : ModuleBuilder() {

    private val moduleContext = RsModuleContext()

    override fun getModuleType() = RsModuleType()

    override fun getBuilderId() = "runescript"

    override fun getPresentableName() = RsBundle.message("module.builder.name")

    override fun getDescription() = RsBundle.message("module.builder.description")

    override fun getCustomOptionsStep(context: WizardContext, parentDisposable: Disposable?): ModuleWizardStep {
        return RsModuleWizardStep(context)
    }

    override fun createWizardSteps(wizardContext: WizardContext, modulesProvider: ModulesProvider): Array<ModuleWizardStep> {
        return emptyArray()
    }


    override fun setupRootModel(modifiableRootModel: ModifiableRootModel) {
        val contentEntry = doAddContentEntry(modifiableRootModel)
        val projectDir = contentEntry?.file ?: return
        val module = modifiableRootModel.module
        RsProjectTemplate().generateTemplate(module, contentEntry, projectDir)
    }

    override fun isSuitableSdkType(sdkType: SdkTypeId?): Boolean {
        return sdkType == RsSdkType.find()
    }

    enum class RsModuleBackendType(val messagePointer: Supplier<String>) {
        NEPTUNE(RsBundle.pointer("module.builder.backend.neptune")),
        RSCP(RsBundle.pointer("module.builder.backend.rscp"))
    }

    private class RsModuleContext : UserDataHolderBase() {
        var backendType: RsModuleBackendType
            get() = getUserData(BACKEND_TYPE_KEY)!!
            set(value) = putUserData(BACKEND_TYPE_KEY, value)

        companion object {
            private val BACKEND_TYPE_KEY = Key.create<RsModuleBackendType>("runescript.backend.type")
        }
    }

    private inner class RsModuleWizardStep(private val wizardContext: WizardContext) : ModuleWizardStep() {
        private val propertyGraph = PropertyGraph()
        private val backendProperty: GraphProperty<RsModuleBackendType> = propertyGraph.property(RsModuleBackendType.NEPTUNE)
        private val neptuneCompilerPathProperty: GraphProperty<String> = propertyGraph.property("")
        private val contentPanel: DialogPanel by lazy { createPanel() }
        private var rscpRows = mutableListOf<Row>()
        private var neptuneRows = mutableListOf<Row>()
        override fun getComponent(): JComponent {
            return contentPanel
        }

        @Suppress("UnstableApiUsage")
        private fun Panel.addBackendTypeRow() {
            row(RsBundle.message("module.builder.backend")) {
                segmentedButton(RsModuleBackendType.values().toList()) { it.messagePointer.get() }.bind(backendProperty)
            }.bottomGap(BottomGap.SMALL)
            moduleContext.backendType = RsModuleBackendType.NEPTUNE
            backendProperty.afterChange {
                moduleContext.backendType = it
                updateVisibility()
            }
        }
        private fun updateVisibility() {
            neptuneRows.forEach { row -> row.visible(moduleContext.backendType == RsModuleBackendType.NEPTUNE) }
            rscpRows.forEach { row -> row.visible(moduleContext.backendType == RsModuleBackendType.RSCP) }
        }

        private fun createPanel(): DialogPanel {
            return panel {
                addBackendTypeRow()
                createNeptunePanel()
                createRscpPanel()
                updateVisibility()
            }.withVisualPadding(true)
        }

        private fun Panel.createRscpPanel() {
            row {
                label("This is a work in progress!")
                rscpRows += this
            }
        }

        private fun Panel.createNeptunePanel() {
            row(RsBundle.message("module.builder.backend.neptune.compiler.path")) {
                val fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFileDescriptor()
                        .withTitle("Select Compiler JAR File")
                        .withDescription("Select the compiler JAR file for Neptune backend")
                        .withFileFilter {
                            it.exists() && it.extension == "jar"
                        }
                textFieldWithBrowseButton("Select Compiler JAR File", wizardContext.project, fileChooserDescriptor)
                        .bindText(neptuneCompilerPathProperty)
                        .horizontalAlign(HorizontalAlign.FILL)
                        .comment("The path must lead to a valid compiler JAR file")
                neptuneRows += this
            }
            row {
                browserLink("Visit the GitLab repository to download the latest JAR file", "https://gitlab.com/neptune-ps/neptune")
                neptuneRows += this
            }
        }

        override fun updateDataModel() {

        }

    }
}
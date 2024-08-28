package io.runescript.plugin.ide.neptune

import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil
import com.intellij.openapi.externalSystem.util.ExternalSystemSettingsControl
import com.intellij.openapi.externalSystem.util.ExternalSystemUiUtil
import com.intellij.openapi.externalSystem.util.PaintAwarePanel
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.openapi.observable.util.toUiPathProperty
import com.intellij.openapi.projectRoots.JavaSdkType
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ui.configuration.SdkComboBox
import com.intellij.openapi.roots.ui.configuration.SdkComboBoxModel
import com.intellij.openapi.roots.ui.configuration.SdkComboBoxModel.Companion.createJdkComboBoxModel
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel
import com.intellij.openapi.ui.BrowseFolderDescriptor.Companion.withPathToTextConvertor
import com.intellij.openapi.ui.BrowseFolderDescriptor.Companion.withTextToPathConvertor
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.ui.getPresentablePath
import com.intellij.openapi.ui.validation.CHECK_DIRECTORY
import com.intellij.openapi.ui.validation.CHECK_NON_EMPTY
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.trimmedTextValidation
import com.intellij.ui.layout.ValidationInfoBuilder
import com.intellij.util.lang.JavaVersion
import com.intellij.vcsUtil.VcsUtil.getCanonicalPath
import java.io.File
import java.util.function.Predicate

class NeptuneSystemSettingsControl(private val settings: NeptuneSettings) :
    ExternalSystemSettingsControl<NeptuneSettings> {

    private val propertyGraph = PropertyGraph()
    private val neptuneHomeProperty = propertyGraph.property("")

    private val jvmComboBoxModel = createJava17SdkModel()
    private val jvmComboBox = SdkComboBox(jvmComboBoxModel)
    private var neptuneHome by neptuneHomeProperty

    private val selectedJdk: String
        get() = jvmComboBox.getSelectedSdk()?.name ?: ""


    private fun createJava17SdkModel(): SdkComboBoxModel {
        val project = settings.project
        val model = ProjectSdksModel()
        model.reset(project)
        val isJava17Sdk = Predicate<Sdk> { sdk ->
            if (sdk.homePath == null) {
                return@Predicate false
            }
            val sdkType = sdk.sdkType
            if (sdkType !is JavaSdkType) {
                return@Predicate false
            }
            val versionString = sdkType.getVersionString(sdk) ?: return@Predicate false
            val version = JavaVersion.parse(versionString)
            version.feature >= 17
        }
        return createJdkComboBoxModel(
            project,
            model,
            null,
            null,
            isJava17Sdk
        )
    }

    override fun fillUi(canvas: PaintAwarePanel, indentLevel: Int) {
        val panel = panel {
            row("Neptune JVM:") {
                cell(jvmComboBox)
                    .align(AlignX.FILL)
                    .comment("The JVM to use for running Neptune.")
            }
            row("Neptune home:") {
                val fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()
                    .withPathToTextConvertor(::getPresentablePath)
                    .withTextToPathConvertor(::getCanonicalPath)
                @Suppress("UnstableApiUsage")
                textFieldWithBrowseButton("Neptune Home:", null, fileChooserDescriptor)
                    .bindText(neptuneHomeProperty.toUiPathProperty())
                    .trimmedTextValidation(CHECK_NON_EMPTY, CHECK_DIRECTORY)
                    .validationInfo { validateNeptuneHome() }
                    .align(AlignX.FILL)
            }
        }

        canvas.add(panel, ExternalSystemUiUtil.getFillLineConstraints(indentLevel))
    }

    private fun ValidationInfoBuilder.validateNeptuneHome(): ValidationInfo? {
        val homeFolder = File(neptuneHome)
        if (!homeFolder.exists() || !homeFolder.isDirectory) {
            return error("The specified Neptune home directory does not exist.")
        }
        val libsFolder = File(homeFolder, "libs")
        if (!libsFolder.exists() || !libsFolder.isDirectory) {
            return error("The specified Neptune home directory is not valid.");
        }
        val compilerJar =
            libsFolder.listFiles { _, name -> name.matches("neptune-clientscript-compiler-.*\\.jar".toRegex()) }
        if (compilerJar.isNullOrEmpty()) {
            return error("The specified Neptune home directory does not contain the compiler jar.")
        }
        return null
    }

    override fun reset() {
        val jre = settings.launcherJre
        if (jre.isNotBlank()) {
            jvmComboBox.setSelectedSdk(jre)
        } else {
            jvmComboBox.selectedItem = jvmComboBox.showNoneSdkItem()
        }
        neptuneHome = settings.neptuneHome
    }

    override fun isModified(): Boolean {
        return settings.launcherJre != selectedJdk
                || ExternalSystemApiUtil.normalizePath(settings.neptuneHome) != ExternalSystemApiUtil.normalizePath(
            neptuneHome
        )
    }

    override fun disposeUIResources() {
        ExternalSystemUiUtil.disposeUi(this)
    }

    override fun showUi(show: Boolean) {
        ExternalSystemUiUtil.showUi(this, show)
    }

    override fun validate(settings: NeptuneSettings): Boolean {
        return true
    }

    override fun apply(settings: NeptuneSettings) {
        settings.launcherJre = selectedJdk
        settings.neptuneHome = ExternalSystemApiUtil.normalizePath(neptuneHome) ?: ""
    }
}
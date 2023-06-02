package io.runescript.plugin.ide.sdk

import com.intellij.compiler.JavaInMemoryCompiler
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.target.TargetEnvironmentsManager
import com.intellij.execution.target.java.JavaLanguageRuntimeConfiguration
import com.intellij.ide.wizard.withVisualPadding
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.projectRoots.*
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.ui.validation.CHECK_NON_EMPTY
import com.intellij.openapi.util.text.StringUtil
import com.intellij.ui.dsl.builder.RightGap
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.trimmedTextValidation
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.ide.RsIcons
import org.jdom.Element
import java.io.File
import javax.swing.JComponent


class RsSdkType : SdkType("RuneScript SDK") {

    override fun loadAdditionalData(currentSdk: Sdk, additional: Element): SdkAdditionalData? {
        return null
    }

    override fun saveAdditionalData(additionalData: SdkAdditionalData, additional: Element) {

    }

    override fun setupSdkPaths(sdk: Sdk) {
    }

    override fun suggestHomePath(): String? {
        return null
    }

    override fun isValidSdkHome(path: String): Boolean {
        return findNeptuneJar(path) != null
    }

    private fun findNeptuneJar(path: String): File? {
        val neptuneFolder = File(path, "lib")
        if (!neptuneFolder.exists()) {
            return null
        }
        val possibleNeptuneJar = neptuneFolder
            .listFiles { file -> file.name.startsWith(NEPTUNE_COMPILER_JAR_PREFIX) }
        if (possibleNeptuneJar == null || possibleNeptuneJar.size != 1) {
            return null
        }
        val neptuneJar = possibleNeptuneJar[0]
        if (!neptuneJar.canExecute()) {
            return null
        }
        return neptuneJar
    }

    override fun suggestSdkName(currentSdkName: String?, sdkHome: String): String {
        return if (findNeptuneJar(sdkHome) != null) "Neptune" else "Unknown"
    }

    override fun getVersionString(sdkHome: String): String? {
        return findNeptuneJar(sdkHome)?.nameWithoutExtension?.substring(NEPTUNE_COMPILER_JAR_PREFIX.length)
    }

    override fun getIcon() = RsIcons.RuneScript

    override fun getDefaultDocumentationUrl(sdk: Sdk) = "https://gitlab.com/neptune-ps/neptune"

    override fun getDownloadSdkUrl() = "https://gitlab.com/neptune-ps/neptune"

    override fun supportsCustomCreateUI() = false

    override fun isRootTypeApplicable(type: OrderRootType): Boolean {
        return false
    }

    override fun createAdditionalDataConfigurable(
        sdkModel: SdkModel,
        sdkModificator: SdkModificator
    ): AdditionalDataConfigurable? {
        return null
    }

    override fun getPresentableName(): String {
        return RsBundle.message("module.sdk.presentation.name")
    }

    companion object {
        private const val NEPTUNE_COMPILER_JAR_PREFIX = "neptune-clientscript-compiler-"
        fun find() = findInstance(RsSdkType::class.java)
    }
}
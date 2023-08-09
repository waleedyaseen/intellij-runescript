package io.runescript.plugin.ide.sdk

import com.intellij.openapi.projectRoots.*
import com.intellij.openapi.roots.OrderRootType
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.ide.RsIcons
import org.jdom.Element
import java.io.File


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
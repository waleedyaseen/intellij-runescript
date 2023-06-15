package io.runescript.plugin.ide.sdk

import com.intellij.codeInsight.daemon.ProjectSdkSetupValidator
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ui.configuration.SdkPopupFactory
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.ui.EditorNotificationPanel
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.ide.filetypes.Cs2FileType
import io.runescript.plugin.oplang.filetypes.RsOpFileType
import io.runescript.plugin.lang.RuneScript


class RsSdkSetupValidator : ProjectSdkSetupValidator {

    override fun isApplicableFor(project: Project, file: VirtualFile): Boolean {
        val fileType = file.fileType
        when (fileType) {
            is Cs2FileType, RsOpFileType -> {
                val psiFile = PsiManager.getInstance(project).findFile(file) ?: return false
                return psiFile.language.isKindOf(RuneScript)
            }

            else -> return false
        }
    }

    override fun getErrorMessage(project: Project, file: VirtualFile): String? {
        val module = ModuleUtilCore.findModuleForFile(file, project)
        if (module == null || module.isDisposed) {
            return null
        }
        val moduleRootManager = ModuleRootManager.getInstance(module)
        val sdk = moduleRootManager.sdk ?: return RsBundle.message("module.sdk.not.defined")
        if (sdk.sdkType != RsSdkType.find()) {
            return RsBundle.message("module.sdk.misconfigured")
        }
        return null
    }

    override fun getFixHandler(project: Project, file: VirtualFile): EditorNotificationPanel.ActionHandler {
        val sdkType = RsSdkType.find()
        return SdkPopupFactory.newBuilder()
            .withProject(project)
            .withSdkTypeFilter { it == sdkType }
            .updateSdkForFile(file)
            .buildEditorNotificationPanelHandler()
    }
}


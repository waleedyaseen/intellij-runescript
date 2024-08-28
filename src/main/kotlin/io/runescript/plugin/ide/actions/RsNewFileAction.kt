package io.runescript.plugin.ide.actions

import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.psi.PsiDirectory
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.ide.RsIcons

class RsNewFileAction :
    CreateFileFromTemplateAction(
        RsBundle.message("action.new.file.text"),
        RsBundle.message("action.new.file.description"),
        RsIcons.Cs2FileType
    ), DumbAware {
    override fun isAvailable(dataContext: DataContext): Boolean {
        if (!super.isAvailable(dataContext)) return false
        val ideView = LangDataKeys.IDE_VIEW.getData(dataContext) ?: return false
        val project = CommonDataKeys.PROJECT.getData(dataContext) ?: return false
        val projectFileIndex = ProjectRootManager.getInstance(project).fileIndex
        return ideView.directories.any {
            /*val module = projectFileIndex.getModuleForFile(it.virtualFile) ?: return@any false
            return ModuleType.get(module) is RsModuleType && */projectFileIndex.isInSourceContent(it.virtualFile)
        }
    }

    override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
        builder.setTitle(RsBundle.message("action.new.file.dialog.title"))
        builder.addKind(
            RsBundle.message("action.new.file.dialog.clientscript.title"),
            RsIcons.Cs2FileType,
            "ClientScript"
        )
    }

    override fun getActionName(directory: PsiDirectory?, newName: String, templateName: String?) =
        RsBundle.message("action.RuneScript.NewFile.text")

    override fun startInWriteAction() = false
}
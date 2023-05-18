package io.runescript.plugin.ide.actions

import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.psi.PsiDirectory
import io.runescript.plugin.ide.RsIcons
import io.runescript.plugin.ide.RuneScriptBundle
import io.runescript.plugin.ide.projectWizard.RsModuleType

class RsNewFileAction :
    CreateFileFromTemplateAction(
        RuneScriptBundle.message("action.new.file.text"),
        RuneScriptBundle.message("action.new.file.description"),
        RsIcons.ClientScript
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
        builder.setTitle(RuneScriptBundle.message("action.new.file.dialog.title"))
        builder.addKind(
            RuneScriptBundle.message("action.new.file.dialog.clientscript.title"),
            RsIcons.ClientScript,
            "ClientScript"
        )
    }

    override fun getActionName(directory: PsiDirectory?, newName: String, templateName: String?) =
        RuneScriptBundle.message("action.RuneScript.NewFile.text")

    override fun startInWriteAction() = false
}
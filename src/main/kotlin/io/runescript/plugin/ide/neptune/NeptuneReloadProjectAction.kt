package io.runescript.plugin.ide.neptune

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project

class NeptuneReloadProjectAction : DumbAwareAction() {
    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(event: AnActionEvent) {
        event.presentation.isEnabledAndVisible = event.project?.let(::canReloadNeptuneProjects) == true
    }

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        FileDocumentManager.getInstance().saveAllDocuments()
        reloadLinkedNeptuneProjects(project) { projectPath ->
            refreshNeptuneProject(project, projectPath)
        }
    }
}

internal fun canReloadNeptuneProjects(project: Project): Boolean = linkedNeptuneProjectPaths(project).isNotEmpty()

internal fun reloadLinkedNeptuneProjects(
    project: Project,
    refreshProject: (String) -> Unit,
) {
    linkedNeptuneProjectPaths(project).forEach(refreshProject)
}

package io.runescript.plugin.ide.execution.build

import com.intellij.ide.trustedProjects.TrustedProjects
import com.intellij.openapi.components.service
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.project.Project
import com.intellij.task.ModuleBuildTask
import com.intellij.task.ProjectTask
import com.intellij.task.ProjectTaskContext
import com.intellij.task.ProjectTaskRunner
import io.runescript.plugin.ide.projectWizard.RsModuleType
import org.jetbrains.concurrency.Promise
import org.jetbrains.concurrency.rejectedPromise

class RsBuildTaskRunner : ProjectTaskRunner() {
    override fun canRun(
        project: Project,
        projectTask: ProjectTask,
        context: ProjectTaskContext?,
    ): Boolean {
        if (projectTask !is ModuleBuildTask) {
            return false
        }
        val moduleType = ModuleType.get(projectTask.module)
        // TODO(Walied): Why the module is resolving to Unknown module type and not RsModuleType?
        return moduleType.id == RsModuleType.ID
    }

    override fun run(
        project: Project,
        context: ProjectTaskContext,
        vararg tasks: ProjectTask,
    ): Promise<Result> {
        if (project.isDisposed) {
            return rejectedPromise("Project is already disposed")
        }
        if (!TrustedProjects.isProjectTrusted(project)) {
            return rejectedPromise("Project is untrusted")
        }
        val moduleTasks = tasks.filterIsInstance<ModuleBuildTask>()
        if (moduleTasks.size != tasks.size) {
            return rejectedPromise("RuneScript build runner only supports module build tasks")
        }
        return project.service<RsBuildService>().build(moduleTasks)
    }
}

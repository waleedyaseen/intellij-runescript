package io.runescript.plugin.ide.neptune

import com.intellij.openapi.application.readAction
import com.intellij.openapi.components.service
import com.intellij.openapi.externalSystem.importing.ImportSpecBuilder
import com.intellij.openapi.externalSystem.service.execution.ProgressExecutionMode
import com.intellij.openapi.externalSystem.service.project.manage.ExternalProjectsManager
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.util.io.FileUtil

class NeptuneProjectRecoveryActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        val externalProjectsManager = readAction { ExternalProjectsManager.getInstance(project) }
        externalProjectsManager.runWhenInitialized {
            refreshMissingNeptuneProjects(project) { projectPath ->
                refreshNeptuneProject(project, projectPath)
            }
        }
    }
}

internal fun refreshNeptuneProject(
    project: Project,
    projectPath: String,
) {
    ExternalSystemUtil.refreshProject(
        projectPath,
        ImportSpecBuilder(project, Neptune.SYSTEM_ID)
            .use(ProgressExecutionMode.IN_BACKGROUND_ASYNC)
            .withImportProjectData(true),
    )
}

internal fun refreshMissingNeptuneProjects(
    project: Project,
    refreshProject: (String) -> Unit,
) {
    if (project.isDisposed) return

    val loadedProjectPaths =
        ModuleManager
            .getInstance(project)
            .modules
            .asSequence()
            .filter { module -> ExternalSystemApiUtil.isExternalSystemAwareModule(Neptune.SYSTEM_ID, module) }
            .mapNotNull { module ->
                ExternalSystemApiUtil.getExternalRootProjectPath(module)
                    ?: ExternalSystemApiUtil.getExternalProjectPath(module)
            }.map(::normalizeNeptuneProjectPath)
            .toList()

    linkedNeptuneProjectPaths(project)
        .asSequence()
        .filterNot { linkedPath -> loadedProjectPaths.any { loadedPath -> FileUtil.pathsEqual(linkedPath, loadedPath) } }
        .forEach(refreshProject)
}

internal fun linkedNeptuneProjectPaths(project: Project): List<String> {
    if (project.isDisposed) return emptyList()

    return project
        .service<NeptuneSettings>()
        .linkedProjectsSettings
        .asSequence()
        .map { settings -> normalizeNeptuneProjectPath(settings.externalProjectPath) }
        .distinctPaths()
        .toList()
}

private fun Sequence<String>.distinctPaths(): Sequence<String> =
    sequence {
        val paths = mutableListOf<String>()
        for (path in this@distinctPaths) {
            if (paths.none { existingPath -> FileUtil.pathsEqual(existingPath, path) }) {
                paths.add(path)
                yield(path)
            }
        }
    }

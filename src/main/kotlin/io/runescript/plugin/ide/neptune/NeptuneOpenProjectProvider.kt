package io.runescript.plugin.ide.neptune

import com.intellij.openapi.externalSystem.importing.AbstractOpenProjectProvider
import com.intellij.openapi.externalSystem.importing.ImportSpecBuilder
import com.intellij.openapi.externalSystem.service.execution.ProgressExecutionMode
import com.intellij.openapi.externalSystem.service.project.trusted.ExternalSystemTrustedProjectDialog
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.nio.file.Path

object NeptuneOpenProjectProvider : AbstractOpenProjectProvider() {
    override val systemId = Neptune.SYSTEM_ID

    override fun isProjectFile(file: VirtualFile): Boolean = file.isNeptuneBuildFile

    override suspend fun linkProject(
        projectFile: VirtualFile,
        project: Project,
    ) {
        val projectRoot = if (projectFile.isDirectory) projectFile else projectFile.parent
        val projectPath = normalizeNeptuneProjectPath(projectRoot.path)
        val trusted =
            ExternalSystemTrustedProjectDialog.confirmLinkingUntrustedProjectAsync(
                project,
                systemId,
                Path.of(projectPath),
            )
        if (!trusted) return

        val settings = NeptuneProjectSettings()
        settings.externalProjectPath = projectPath

        val importSpec =
            ImportSpecBuilder(project, systemId)
                .use(ProgressExecutionMode.IN_BACKGROUND_ASYNC)
                .withImportProjectData(true)
        ExternalSystemUtil.linkExternalProject(settings, importSpec)
    }
}

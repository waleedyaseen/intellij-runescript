package io.runescript.plugin.ide.neptune

import com.intellij.openapi.externalSystem.importing.AbstractOpenProjectProvider
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import kotlin.io.path.absolutePathString

object NeptuneOpenProjectProvider : AbstractOpenProjectProvider() {
    override val systemId = Neptune.SYSTEM_ID

    override fun isProjectFile(file: VirtualFile): Boolean  {
        return file.isNeptuneBuildFile
    }

    override suspend fun linkProject(
        projectFile: VirtualFile,
        project: Project
    ) {
        val projectRoot = if (projectFile.isDirectory) projectFile else projectFile.parent

        val settings = NeptuneProjectSettings()
        settings.externalProjectPath = projectRoot.toNioPath().absolutePathString()

        ExternalSystemApiUtil.getSettings(project, Neptune.SYSTEM_ID).linkProject(settings)
    }
}
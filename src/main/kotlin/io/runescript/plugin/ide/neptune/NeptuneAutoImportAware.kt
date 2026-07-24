package io.runescript.plugin.ide.neptune

import com.intellij.openapi.externalSystem.ExternalSystemAutoImportAware
import com.intellij.openapi.project.Project
import java.nio.file.Path

class NeptuneAutoImportAware : ExternalSystemAutoImportAware {
    override fun getAffectedExternalProjectPath(
        changedFileOrDirPath: String,
        project: Project,
    ): String? {
        val changedPath = Path.of(changedFileOrDirPath)
        if (changedPath.fileName?.toString() != "neptune.toml") {
            return null
        }

        return changedPath.parent?.toString()
    }

    override fun getAffectedExternalProjectFilePaths(
        projectPath: String,
        project: Project,
    ): List<Path> = listOf(Path.of(projectPath, "neptune.toml"))
}

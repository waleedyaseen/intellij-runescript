package io.runescript.plugin.ide.neptune

import com.intellij.openapi.components.service
import com.intellij.openapi.externalSystem.ExternalSystemAutoImportAware
import com.intellij.openapi.project.Project
import java.io.File
import java.util.*

class NeptuneAutoImportAware : ExternalSystemAutoImportAware {

    override fun getAffectedExternalProjectPath(changedFileOrDirPath: String, project: Project): String? {
        if (changedFileOrDirPath != "neptune.toml") {
            return null
        }

        val file = File(changedFileOrDirPath)
        if (file.isDirectory) {
            return null
        }

        return file.parent
    }

    override fun getAffectedExternalProjectFiles(projectPath: String, project: Project): MutableList<File>? {
        val settings = project.service<NeptuneSettings>()
        val projectSettings = settings.getLinkedProjectSettings(projectPath) ?: return null
        val neptuneToml = File(projectSettings.externalProjectPath, "neptune.toml")
        return Collections.singletonList(neptuneToml)
    }
}
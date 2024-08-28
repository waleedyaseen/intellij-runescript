package io.runescript.plugin.ide.neptune

import com.intellij.openapi.externalSystem.service.project.wizard.AbstractExternalProjectImportProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.projectImport.ProjectImportBuilder

class NeptuneProjectImportProvider : AbstractExternalProjectImportProvider(
    ProjectImportBuilder.EXTENSIONS_POINT_NAME.findExtensionOrFail(NeptuneProjectImportBuilder::class.java),
    Neptune.SYSTEM_ID
) {
    override fun canImport(fileOrDirectory: VirtualFile, project: Project?): Boolean {
        if (fileOrDirectory.isDirectory) {
           return fileOrDirectory.children.any { it.isNeptuneBuildFile }
        }
        return fileOrDirectory.isNeptuneBuildFile
    }
}
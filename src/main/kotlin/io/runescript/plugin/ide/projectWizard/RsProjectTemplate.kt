package io.runescript.plugin.ide.projectWizard

import com.intellij.openapi.module.Module
import com.intellij.openapi.roots.ContentEntry
import com.intellij.openapi.vfs.VirtualFile

class RsProjectTemplate {

    fun generateTemplate(module: Module, contentEntry: ContentEntry, projectDir: VirtualFile) {
        val gitIgnoreFile = projectDir.createChildData(this, ".gitignore")
        val srcDir = projectDir.createChildDirectory(projectDir, "src")
        contentEntry.addSourceFolder(srcDir, false)
        val packDir = projectDir.createChildDirectory(projectDir, "pack")
        contentEntry.addExcludeFolder(packDir)
        projectDir.createChildDirectory(projectDir, "sym")
        gitIgnoreFile.setBinaryContent(
            """
            # IntelliJ project files
            .idea/
            *.iml
            
            # RuneScript project files
            pack/
        """.trimIndent().toByteArray()
        )
    }
}
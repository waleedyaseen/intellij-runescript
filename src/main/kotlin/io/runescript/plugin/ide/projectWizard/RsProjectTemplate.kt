package io.runescript.plugin.ide.projectWizard

import com.intellij.openapi.module.Module
import com.intellij.openapi.roots.ContentEntry
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.findOrCreateDirectory

class RsProjectTemplate {

    fun generateTemplate(module: Module, contentEntry: ContentEntry, projectDir: VirtualFile) {
        createGitIgnoreFile(projectDir)
        createEditorConfigFile(projectDir)
        val srcDir = projectDir.findChild("src") ?: projectDir.createChildDirectory(projectDir, "src")
        contentEntry.addSourceFolder(srcDir, false)
        val packDir = projectDir.findChild("pack") ?: projectDir.createChildDirectory(projectDir, "pack")
        contentEntry.addExcludeFolder(packDir)
        projectDir.findOrCreateDirectory("symbols")
    }

    private fun createGitIgnoreFile(projectDir: VirtualFile) {
        var gitIgnore = projectDir.findChild(".gitignore")
        if (gitIgnore != null) return
        gitIgnore = projectDir.createChildData(projectDir, ".gitignore")
        gitIgnore.setBinaryContent(
            """
            # IntelliJ project files
            .idea/
            *.iml
            out/
            
            # RuneScript project files
            pack/
        """.trimIndent().toByteArray()
        )
    }

    private fun createEditorConfigFile(projectDir: VirtualFile) {
        var gitIgnore = projectDir.findChild(".editorconfig")
        if (gitIgnore != null) return
        gitIgnore = projectDir.createChildData(projectDir, ".editorconfig")
        gitIgnore.setBinaryContent(
            """
            root = true

            [*]
            charset = utf-8
            insert_final_newline = true
            trim_trailing_whitespace = true
            max_line_length = 120
            indent_style = space
            indent_size = 4

            [symbols/*.sym]
            indent_style = tab
            trim_trailing_whitespace = false
        """.trimIndent().toByteArray()
        )
    }
}
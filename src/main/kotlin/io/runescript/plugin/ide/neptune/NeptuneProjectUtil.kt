package io.runescript.plugin.ide.neptune

import com.intellij.openapi.module.Module
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.isFile
import java.nio.file.Path

fun Module.findNeptuneProjectRoot(file: VirtualFile? = null): VirtualFile? {
    val contentRoots = ModuleRootManager.getInstance(this).contentRoots
    if (file == null) return contentRoots.firstOrNull(VirtualFile::hasNeptuneBuildFile)

    val contentRoot = contentRoots.firstOrNull { root -> VfsUtilCore.isAncestor(root, file, false) } ?: return null
    var directory = file.takeIf(VirtualFile::isDirectory) ?: file.parent
    while (directory != null && VfsUtilCore.isAncestor(contentRoot, directory, false)) {
        if (directory.hasNeptuneBuildFile()) return directory
        if (directory === contentRoot) break
        directory = directory.parent
    }
    return null
}

private fun VirtualFile.hasNeptuneBuildFile(): Boolean = findChild("neptune.toml")?.isNeptuneBuildFile == true

val VirtualFile.isNeptuneBuildFile: Boolean
    get() = isFile && exists() && name == "neptune.toml"

internal fun normalizeNeptuneProjectPath(path: String): String {
    var normalized = Path.of(path).toAbsolutePath().normalize()
    if (normalized.fileName?.toString() == "neptune.toml") {
        normalized = normalized.parent
    }
    return normalized.toString()
}

internal fun NeptuneProjectSettings.matchesProjectPath(path: String): Boolean =
    FileUtil.pathsEqual(
        normalizeNeptuneProjectPath(externalProjectPath),
        normalizeNeptuneProjectPath(path),
    )

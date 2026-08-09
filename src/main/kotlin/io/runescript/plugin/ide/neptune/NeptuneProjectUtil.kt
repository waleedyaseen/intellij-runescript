package io.runescript.plugin.ide.neptune

import com.intellij.openapi.module.Module
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.isFile
import java.nio.file.Path

fun Module.findNeptuneProjectRoot(): VirtualFile? {
    val moduleManager = ModuleRootManager.getInstance(this)
    val contentRoots = moduleManager.contentRoots
    return contentRoots.firstOrNull { it.findChild("neptune.toml") != null }
}

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

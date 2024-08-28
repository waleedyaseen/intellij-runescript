package io.runescript.plugin.ide.neptune

import com.intellij.openapi.module.Module
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.isFile

fun Module.findNeptuneProjectRoot(): VirtualFile? {
    val moduleManager = ModuleRootManager.getInstance(this)
    val contentRoots = moduleManager.contentRoots
    return contentRoots.firstOrNull { it.findChild("neptune.toml") != null }
}

val VirtualFile.isNeptuneBuildFile: Boolean
    get() = isFile && exists() && name == "neptune.toml"
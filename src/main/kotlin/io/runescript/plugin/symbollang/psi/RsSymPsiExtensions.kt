package io.runescript.plugin.symbollang.psi

import com.intellij.openapi.vfs.VirtualFile

fun VirtualFile.isConstantFile() = name == "constant.sym"
fun VirtualFile.isVarFile() = name == "varp.sym" || name == "varc.sym"
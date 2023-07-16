package io.runescript.plugin.symbollang.psi

import com.intellij.openapi.vfs.VirtualFile

fun VirtualFile.isConstantFile() = name == "constant.tsv"
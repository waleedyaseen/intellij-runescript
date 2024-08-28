package io.runescript.plugin.symbollang.psi

import com.intellij.openapi.vfs.VirtualFile

fun VirtualFile.isSymbolFileOfTypeLiteral(typeLiteral: String): Boolean {
    val parent = parent
    return if (parent != null && parent.name == typeLiteral) true else nameWithoutExtension == typeLiteral
}

fun VirtualFile.isConstantFile() = isSymbolFileOfTypeLiteral("constant")
fun VirtualFile.isVarFile() = when {
    isSymbolFileOfTypeLiteral("varp") -> true
    isSymbolFileOfTypeLiteral("varc") -> true
    else -> false
}

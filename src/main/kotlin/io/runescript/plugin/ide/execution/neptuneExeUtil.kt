package io.runescript.plugin.ide.execution

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.projectRoots.JavaSdk
import com.intellij.openapi.projectRoots.Sdk
import java.io.File

fun createNeptuneJvmCommand(
    javaSdk: Sdk,
    neptuneHome: File,
    workDirectory: String
) = createNeptuneJvmCommand(JavaSdk.getInstance().getVMExecutablePath(javaSdk), neptuneHome, workDirectory)

fun createNeptuneJvmCommand(
    vmExecutablePath: String,
    neptuneHome: File,
    workDirectory: String
) = GeneralCommandLine()
    .withExePath(vmExecutablePath)
    .withParameters(createNeptuneJvmArgs(neptuneHome.toString()))
    .withWorkDirectory(workDirectory)
    .withEnvironment(System.getenv())

private fun createNeptuneJvmArgs(
    homeDirectory: String
): MutableList<String> {
    val args = mutableListOf<String>()
    args += "-classpath"
    args += "$homeDirectory${File.separator}lib${File.separator}*"
    args += "me.filby.neptune.clientscript.compiler.ClientScriptCompilerApplicationKt"
    return args
}
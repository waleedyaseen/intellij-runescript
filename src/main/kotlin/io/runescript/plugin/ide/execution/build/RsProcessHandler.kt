package io.runescript.plugin.ide.execution.build

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.KillableProcessHandler

class RsProcessHandler(commandLine: GeneralCommandLine) : KillableProcessHandler(commandLine) {

    init {
        setShouldDestroyProcessRecursively(true)
    }
}
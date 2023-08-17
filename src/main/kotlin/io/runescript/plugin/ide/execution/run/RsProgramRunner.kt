package io.runescript.plugin.ide.execution.run

import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.configurations.RunnerSettings
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.GenericProgramRunner
import com.intellij.execution.runners.executeState
import com.intellij.execution.ui.RunContentDescriptor

class RsProgramRunner : GenericProgramRunner<RunnerSettings>() {

    override fun getRunnerId() = ID

    override fun canRun(executorId: String, profile: RunProfile): Boolean {
        return executorId == DefaultRunExecutor.EXECUTOR_ID && profile is RsRunConfiguration
    }

    override fun doExecute(state: RunProfileState, environment: ExecutionEnvironment): RunContentDescriptor? {
        return executeState(state, environment, this)
    }

    companion object {
        const val ID = "RsProgramRunner"
    }
}
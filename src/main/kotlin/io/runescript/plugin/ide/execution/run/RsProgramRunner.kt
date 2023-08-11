package io.runescript.plugin.ide.execution.run

import com.intellij.execution.ExecutionManager
import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.configurations.RunnerSettings
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.runners.executeState
import org.jetbrains.concurrency.resolvedPromise

class RsProgramRunner : ProgramRunner<RunnerSettings> {

    override fun getRunnerId() = ID

    override fun canRun(executorId: String, profile: RunProfile): Boolean {
        return executorId == DefaultRunExecutor.EXECUTOR_ID && profile is RsRunConfiguration
    }

    override fun execute(environment: ExecutionEnvironment) {
        val state = environment.state ?: return
        @Suppress("UnstableApiUsage")
        ExecutionManager.getInstance(environment.project).startRunProfile(environment) {
            resolvedPromise(executeState(state, environment, this))
        }
    }

    companion object {
        const val ID = "RsProgramRunner"
    }
}
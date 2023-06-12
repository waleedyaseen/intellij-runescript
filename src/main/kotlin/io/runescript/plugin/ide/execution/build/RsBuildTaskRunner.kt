package io.runescript.plugin.ide.execution.build

import com.intellij.execution.ExecutorRegistry
import com.intellij.execution.RunManager
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.ide.impl.isTrusted
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessModuleDir
import com.intellij.openapi.projectRoots.JavaSdkType
import com.intellij.openapi.projectRoots.JavaSdkVersion
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.ex.JavaSdkUtil
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.task.ModuleBuildTask
import com.intellij.task.ProjectTask
import com.intellij.task.ProjectTaskContext
import com.intellij.task.ProjectTaskRunner
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.ide.execution.run.RsProgramRunner
import io.runescript.plugin.ide.execution.run.RsRunConfigurationType
import io.runescript.plugin.ide.sdk.RsSdkType
import org.jetbrains.concurrency.AsyncPromise
import org.jetbrains.concurrency.Promise
import org.jetbrains.concurrency.rejectedPromise
import kotlin.io.path.absolutePathString


class RsBuildTaskRunner : ProjectTaskRunner() {

    override fun canRun(projectTask: ProjectTask): Boolean {
        return projectTask is ModuleBuildTask
    }

    override fun run(project: Project, context: ProjectTaskContext, vararg tasks: ProjectTask): Promise<Result> {
        if (project.isDisposed) {
            return rejectedPromise("Project is already disposed")
        }
        @Suppress("UnstableApiUsage")
        if (!project.isTrusted()) {
            return rejectedPromise("Project is untrusted")
        }
        val promise = AsyncPromise<Result>()
        for (task in tasks) {
            runTask(project, task, promise)
        }
        return promise
    }

    private fun runTask(project: Project, task: ProjectTask, promise: AsyncPromise<Result>) {
        require(task is ModuleBuildTask)
        val moduleRootManager = ModuleRootManager.getInstance(task.module)
        val sdk = moduleRootManager.sdk
        if (sdk == null) {
            val notification = buildNotificationGroup().createNotification(
                RsBundle.message("module.sdk.not.defined"),
                NotificationType.ERROR
            )
            notification.notify(project)
            return
        }
        if (sdk.sdkType != RsSdkType.find()) {
            val notification = buildNotificationGroup().createNotification(
                RsBundle.message("module.sdk.misconfigured"),
                NotificationType.ERROR
            )
            notification.notify(project)
            return
        }
        val javaSdk = ProjectJdkTable.getInstance()
            .allJdks
            .firstOrNull { it.sdkType is JavaSdkType && JavaSdkUtil.isJdkAtLeast(it, JavaSdkVersion.JDK_17) }
        if (javaSdk == null) {
            val notification = buildNotificationGroup().createNotification(
                RsBundle.message("build.notification.jdk.not.found.title"),
                RsBundle.message("build.notification.jdk.not.found.content"),
                NotificationType.ERROR
            )
            notification.notify(project)
            promise.setError("Could not find JDK 17")
            return
        }
        val runManager = RunManager.getInstance(project)
        val executor = ExecutorRegistry.getInstance().getExecutorById(DefaultRunExecutor.EXECUTOR_ID) ?: return
        val runner = ProgramRunner.findRunnerById(RsProgramRunner.ID) ?: return
        val settings = createBuildSettings(runManager)
        val environment = ExecutionEnvironment(executor, runner, settings, project)
        val workDirectory = task.module
            .guessModuleDir()!!
            .toNioPath()
            .absolutePathString()
        val buildInstance = RsBuildInstance(environment, Any(), task.module, workDirectory, sdk, javaSdk)
        buildInstance.build()
    }

    private fun createBuildSettings(runManager: RunManager): RunnerAndConfigurationSettings {
        val type = ConfigurationTypeUtil.findConfigurationType(RsRunConfigurationType::class.java)
        val factory = type.configurationFactories.single()
        return runManager.createConfiguration("build", factory)
    }

    companion object {
        fun buildNotificationGroup(): NotificationGroup {
            return NotificationGroupManager.getInstance().getNotificationGroup("RuneScript Build")
        }
    }
}
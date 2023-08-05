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
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessModuleDir
import com.intellij.openapi.projectRoots.JavaSdkType
import com.intellij.openapi.projectRoots.JavaSdkVersion
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.ex.JavaSdkUtil
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.task.ModuleBuildTask
import com.intellij.task.ProjectTask
import com.intellij.task.ProjectTaskContext
import com.intellij.task.ProjectTaskRunner
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.ide.execution.run.RsProgramRunner
import io.runescript.plugin.ide.execution.run.RsRunConfigurationType
import io.runescript.plugin.ide.projectWizard.RsModuleType
import io.runescript.plugin.ide.sdk.RsSdkType
import org.jetbrains.concurrency.AsyncPromise
import org.jetbrains.concurrency.Promise
import org.jetbrains.concurrency.rejectedPromise
import kotlin.io.path.absolutePathString


class RsBuildTaskRunner : ProjectTaskRunner() {

    override fun canRun(projectTask: ProjectTask): Boolean {
        if (projectTask !is ModuleBuildTask) {
            return false
        }
        val moduleType = ModuleType.get(projectTask.module)
        // TODO(Walied): Why the module is resolving to Unknown module type and not RsModuleType?
        return moduleType.id == RsModuleType.ID
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
        val buildingTask = createBuildTask(project, task, sdk, javaSdk)
        buildingTask.queue()
    }

    private fun createBuildTask(project: Project, task: ModuleBuildTask, sdk: Sdk, javaSdk: Sdk): Task.Backgroundable {
        return object : Task.Backgroundable(project, "Building", false) {
            override fun run(indicator: ProgressIndicator) {
                indicator.text = "Building..."
                indicator.text2 = ""
                try {
                    val runManager = RunManager.getInstance(project)
                    val executor = ExecutorRegistry.getInstance().getExecutorById(DefaultRunExecutor.EXECUTOR_ID)
                        ?: return
                    val runner = ProgramRunner.findRunnerById(RsProgramRunner.ID) ?: return
                    val settings = createBuildSettings(runManager)
                    val environment = ExecutionEnvironment(executor, runner, settings, project)
                    val workDirectory = task.module
                        .guessModuleDir()!!
                        .toNioPath()
                        .absolutePathString()
                    val buildInstance = RsBuildInstance(environment, Any(), task.module, workDirectory, sdk, javaSdk)
                    buildInstance.build().get()
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
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
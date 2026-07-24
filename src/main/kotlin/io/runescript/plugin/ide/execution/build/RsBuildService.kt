package io.runescript.plugin.ide.execution.build

import com.intellij.execution.ExecutorRegistry
import com.intellij.execution.RunManager
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessModuleDir
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.task.ModuleBuildTask
import com.intellij.task.ProjectTaskRunner
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.ide.execution.run.RsProgramRunner
import io.runescript.plugin.ide.execution.run.RsRunConfigurationType
import io.runescript.plugin.ide.neptune.NeptuneSettings
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.concurrency.AsyncPromise
import org.jetbrains.concurrency.Promise
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.io.path.absolutePathString

@Service(Service.Level.PROJECT)
class RsBuildService(
    private val project: Project,
    private val coroutineScope: CoroutineScope,
) {
    fun build(tasks: List<ModuleBuildTask>): Promise<ProjectTaskRunner.Result> {
        val promise = AsyncPromise<ProjectTaskRunner.Result>()
        val job =
            coroutineScope.launch(CoroutineName("RuneScript build")) {
                completeBuild(tasks, promise)
            }
        job.invokeOnCompletion { error ->
            if (error is CancellationException) {
                promise.setResult(RsBuildResult.ABORTED)
            }
        }
        promise.onError { error ->
            if (error is CancellationException) {
                job.cancel()
            }
        }
        return promise
    }

    private suspend fun completeBuild(
        tasks: List<ModuleBuildTask>,
        promise: AsyncPromise<ProjectTaskRunner.Result>,
    ) {
        try {
            var successful = true
            for (task in tasks) {
                if (!buildTask(task)) {
                    successful = false
                }
            }
            promise.setResult(if (successful) RsBuildResult.SUCCESS else RsBuildResult.FAILURE)
        } catch (error: CancellationException) {
            promise.setResult(RsBuildResult.ABORTED)
            throw error
        } catch (error: Throwable) {
            LOG.error("RuneScript build failed", error)
            promise.setResult(RsBuildResult.FAILURE)
        }
    }

    private suspend fun buildTask(task: ModuleBuildTask): Boolean {
        val settings = project.service<NeptuneSettings>()
        val javaSdk =
            ProjectJdkTable
                .getInstance()
                .findJdk(settings.launcherJre)
        if (javaSdk == null) {
            notifyError(
                RsBundle.message("build.notification.jdk.not.found.title"),
                RsBundle.message("build.notification.jdk.not.found.content"),
            )
            return false
        }
        val neptuneHome = File(settings.neptuneHome)
        if (!neptuneHome.exists() || !neptuneHome.isDirectory) {
            notifyError(
                RsBundle.message("build.notification.neptune.home.not.found.title"),
                RsBundle.message("build.notification.neptune.home.not.found.content"),
            )
            return false
        }

        val runManager = RunManager.getInstance(project)
        val executor =
            ExecutorRegistry.getInstance().getExecutorById(DefaultRunExecutor.EXECUTOR_ID)
                ?: return false
        val runner = ProgramRunner.findRunnerById(RsProgramRunner.ID) ?: return false
        val buildSettings = createBuildSettings(runManager)
        val environment = ExecutionEnvironment(executor, runner, buildSettings, project)
        val moduleDirectory = task.module.guessModuleDir() ?: return false
        val workDirectory = moduleDirectory.toNioPath().absolutePathString()
        val buildInstance =
            RsBuildInstance(
                environment,
                Any(),
                task.module,
                workDirectory,
                neptuneHome,
                javaSdk,
            )
        return buildInstance.build()
    }

    private fun createBuildSettings(runManager: RunManager): RunnerAndConfigurationSettings {
        val type = ConfigurationTypeUtil.findConfigurationType(RsRunConfigurationType::class.java)
        val factory = type.configurationFactories.single()
        return runManager.createConfiguration("build", factory)
    }

    private fun notifyError(
        title: String,
        content: String,
    ) {
        buildNotificationGroup()
            .createNotification(title, content, NotificationType.ERROR)
            .notify(project)
    }

    private companion object {
        val LOG = LoggerFactory.getLogger(RsBuildService::class.java)
    }
}

private fun buildNotificationGroup(): NotificationGroup = NotificationGroupManager.getInstance().getNotificationGroup("RuneScript Build")

private enum class RsBuildResult(
    private val aborted: Boolean,
    private val errors: Boolean,
) : ProjectTaskRunner.Result {
    SUCCESS(false, false),
    FAILURE(false, true),
    ABORTED(true, false),
    ;

    override fun isAborted(): Boolean = aborted

    override fun hasErrors(): Boolean = errors
}

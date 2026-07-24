package io.runescript.plugin.ide.execution.build

import com.intellij.build.BuildContentManager
import com.intellij.build.BuildViewManager
import com.intellij.execution.ExecutionListener
import com.intellij.execution.ExecutionManager
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.application.EDT
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.vfs.newvfs.ManagingFS
import com.intellij.openapi.wm.ToolWindow
import io.runescript.plugin.ide.execution.createNeptuneJvmCommand
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.job
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

class RsBuildInstance(
    val environment: ExecutionEnvironment,
    val buildId: Any,
    val module: Module,
    val workDirectory: String,
    private val neptuneHome: File,
    private val javaSdk: Sdk,
) {
    val project: Project
        get() = environment.project
    val executorId: String
        get() = environment.executor.id
    val executionPublisher: ExecutionListener
        get() {
            val messageBus = environment.project.messageBus
            return messageBus.syncPublisher(ExecutionManager.EXECUTION_TOPIC)
        }

    var processHandler: ProcessHandler? = null
    var errorCount = AtomicInteger()

    suspend fun build(): Boolean {
        executionPublisher.processStartScheduled(executorId, environment)
        executionPublisher.processStarting(executorId, environment)
        var processLifecycleStarted = false
        return try {
            withContext(Dispatchers.EDT) {
                FileDocumentManager.getInstance().saveAllDocuments()
                openBuildToolWindow()
            }
            withContext(Dispatchers.IO) {
                ManagingFS.getInstance().flushPendingUpdates()
                processHandler = createProcessHandler()
            }
            val processHandler = checkNotNull(processHandler)
            val completion = CompletableDeferred<Boolean>(currentCoroutineContext().job)
            processLifecycleStarted = true
            val processAdapter = RsBuildProcessAdapter(this, project.service<BuildViewManager>(), completion)
            processHandler.addProcessListener(processAdapter)
            processHandler.startNotify()
            completion.await()
        } catch (error: Throwable) {
            if (!processLifecycleStarted) {
                executionPublisher.processNotStarted(executorId, environment, error)
            }
            throw error
        } finally {
            processHandler?.let {
                if (!it.isProcessTerminated) {
                    it.destroyProcess()
                }
            }
        }
    }

    private fun openBuildToolWindow(): ToolWindow {
        val toolWindow = BuildContentManager.getInstance(project).getOrCreateToolWindow()
        toolWindow.setAvailable(true, null)
        toolWindow.activate(null)
        return toolWindow
    }

    private fun createProcessHandler(): RsProcessHandler {
        val commandLine = createCommandLine()
        val processHandler = RsProcessHandler(commandLine)
        ProcessTerminatedListener.attach(processHandler)
        return processHandler
    }

    private fun createCommandLine(): GeneralCommandLine = createNeptuneJvmCommand(javaSdk, neptuneHome, workDirectory)
}

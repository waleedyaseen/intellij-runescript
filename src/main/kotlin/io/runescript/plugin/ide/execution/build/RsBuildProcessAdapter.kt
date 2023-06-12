package io.runescript.plugin.ide.execution.build

import com.intellij.build.BuildContentDescriptor
import com.intellij.build.BuildProgressListener
import com.intellij.build.DefaultBuildDescriptor
import com.intellij.build.events.EventResult
import com.intellij.build.events.impl.FailureResultImpl
import com.intellij.build.events.impl.FinishBuildEventImpl
import com.intellij.build.events.impl.StartBuildEventImpl
import com.intellij.build.events.impl.SuccessResultImpl
import com.intellij.build.output.BuildOutputInstantReaderImpl
import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.openapi.util.Key
import com.intellij.util.ThreeState
import io.runescript.plugin.ide.RsBundle
import java.util.concurrent.CompletableFuture
import javax.swing.JComponent

class RsBuildProcessAdapter(
    private val instance: RsBuildInstance,
    private val buildProgressListener: BuildProgressListener,
    val future: CompletableFuture<Any>
) : ProcessAdapter() {
    private val instantReader = BuildOutputInstantReaderImpl(
        instance.buildId,
        instance.buildId,
        buildProgressListener,
        listOf(RsBuildOutputParser(instance))
    )

    init {
        instance.executionPublisher.processStarted(instance.executorId, instance.environment, instance.processHandler!!)

        val buildContentDescriptor = BuildContentDescriptor(null, null, object : JComponent() {}, "Build")
        buildContentDescriptor.isActivateToolWindowWhenAdded = true
        buildContentDescriptor.isActivateToolWindowWhenFailed = true
        buildContentDescriptor.isNavigateToError = ThreeState.UNSURE

        val buildDescriptor = DefaultBuildDescriptor(instance.buildId, instance.project.name, instance.workDirectory, System.currentTimeMillis())
            .withContentDescriptor { buildContentDescriptor }

        val buildStarted = StartBuildEventImpl(buildDescriptor, RsBundle.message("build.status.running"))
        buildProgressListener.onEvent(instance.buildId, buildStarted)
    }

    override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
        instantReader.append(event.text)
    }

    override fun processWillTerminate(event: ProcessEvent, willBeDestroyed: Boolean) {
        instance.executionPublisher.processTerminating(instance.executorId, instance.environment, event.processHandler)
    }

    override fun processTerminated(event: ProcessEvent) {
        instantReader.closeAndGetFuture().whenComplete { _, _ ->
            val isSuccessfulBuild = event.exitCode == 0 && instance.errorCount.get() == 0
            val finishEvent = if (isSuccessfulBuild) {
                createFinishEvent(RsBundle.message("build.status.finished"), SuccessResultImpl())
            } else {
                createFinishEvent(RsBundle.message("build.status.failed"), FailureResultImpl())
            }
            // createFinishEvent(RsBundle.message("build.status.cancelled"), SkippedResultImpl())
            buildProgressListener.onEvent(instance.buildId, finishEvent)
            instance.executionPublisher.processTerminated(instance.executorId, instance.environment, event.processHandler, event.exitCode)
            future.complete(Any())
        }
    }

    private fun createFinishEvent(message: String, result: EventResult): FinishBuildEventImpl {
        return FinishBuildEventImpl(
            instance.buildId,
            null,
            System.currentTimeMillis(),
            message,
            result
        )
    }
}
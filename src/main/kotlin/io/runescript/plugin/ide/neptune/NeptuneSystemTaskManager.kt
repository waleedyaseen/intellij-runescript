package io.runescript.plugin.ide.neptune

import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskId
import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskNotificationListener
import com.intellij.openapi.externalSystem.task.ExternalSystemTaskManager

class NeptuneSystemTaskManager : ExternalSystemTaskManager<NeptuneExecutionSettings> {
    override fun cancelTask(id: ExternalSystemTaskId, listener: ExternalSystemTaskNotificationListener): Boolean {
        return false
    }
}
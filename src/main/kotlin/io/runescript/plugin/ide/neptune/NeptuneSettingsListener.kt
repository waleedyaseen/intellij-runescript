package io.runescript.plugin.ide.neptune

import com.intellij.openapi.externalSystem.settings.ExternalSystemSettingsListener
import com.intellij.util.messages.Topic
import com.intellij.util.messages.Topic.ProjectLevel


interface NeptuneSettingsListener : ExternalSystemSettingsListener<NeptuneProjectSettings> {
    companion object {
        @ProjectLevel
        val TOPIC = Topic(NeptuneSettingsListener::class.java, Topic.BroadcastDirection.NONE)
    }
}
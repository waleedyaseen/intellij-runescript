package io.runescript.plugin.ide.neptune

import com.intellij.openapi.externalSystem.settings.ExternalProjectSettings

class NeptuneProjectSettings : ExternalProjectSettings(){

    override fun clone(): ExternalProjectSettings {
        val clone = NeptuneProjectSettings()
        copyTo(clone)
        return clone
    }
}
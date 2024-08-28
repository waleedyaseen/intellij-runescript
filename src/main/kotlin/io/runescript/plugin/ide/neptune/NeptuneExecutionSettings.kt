package io.runescript.plugin.ide.neptune

import com.intellij.openapi.externalSystem.model.settings.ExternalSystemExecutionSettings

class NeptuneExecutionSettings(
    val jreHome: String,
    val neptuneSdkHome: String,
) : ExternalSystemExecutionSettings()
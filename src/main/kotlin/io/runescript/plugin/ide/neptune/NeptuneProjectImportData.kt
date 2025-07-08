package io.runescript.plugin.ide.neptune

import com.intellij.openapi.externalSystem.model.project.AbstractExternalEntityData

class NeptuneProjectImportData(
    val name: String,
    val arraysV2: Boolean
) : AbstractExternalEntityData(Neptune.SYSTEM_ID)
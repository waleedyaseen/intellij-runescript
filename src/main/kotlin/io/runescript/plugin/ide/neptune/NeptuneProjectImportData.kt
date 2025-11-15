package io.runescript.plugin.ide.neptune

import com.intellij.openapi.externalSystem.model.project.AbstractExternalEntityData

class NeptuneProjectImportData(
    val name: String,
    val dbFindReturnsCount: Boolean,
    val ccCreateAssertNewArg: Boolean,
    val prefixPostfixExpressions: Boolean,
    val arraysV2: Boolean,
    val simplifiedTypeCodes: Boolean,
) : AbstractExternalEntityData(Neptune.SYSTEM_ID)
package io.runescript.plugin.ide.neptune

import com.intellij.openapi.externalSystem.model.project.AbstractExternalEntityData

class NeptuneProjectImportData(
    val name: String,
    val sourcePaths: List<String>,
    val symbolPaths: List<String>,
    val dbFindReturnsCount: Boolean,
    val ccCreateAssertNewArg: Boolean,
    val prefixPostfixExpressions: Boolean,
    val arraysV2: Boolean,
    val simplifiedTypeCodes: Boolean,
    val longSupport: Boolean,
) : AbstractExternalEntityData(Neptune.SYSTEM_ID) {
    constructor(
        name: String,
        sourcePaths: List<String>,
        symbolPaths: List<String>,
        dbFindReturnsCount: Boolean,
        ccCreateAssertNewArg: Boolean,
        prefixPostfixExpressions: Boolean,
        arraysV2: Boolean,
        simplifiedTypeCodes: Boolean,
    ) : this(
        name,
        sourcePaths,
        symbolPaths,
        dbFindReturnsCount,
        ccCreateAssertNewArg,
        prefixPostfixExpressions,
        arraysV2,
        simplifiedTypeCodes,
        false,
    )
}

package io.runescript.plugin.ide.neptune

import com.intellij.openapi.components.*
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtil
import com.intellij.psi.PsiElement

@State(
    name = "NeptuneModuleData",
    storages = [Storage(StoragePathMacros.MODULE_FILE)]
)
class NeptuneModuleData : SerializablePersistentStateComponent<NeptuneModuleData.State>(State()) {

    data class State(
        var arraysV2: Boolean = false
    )

    val arraysV2: Boolean
        get() = state.arraysV2

    fun updateFromImportData(importData: NeptuneProjectImportData) = updateState {
        it.arraysV2 = importData.arraysV2
        it
    }
}

val Module.neptuneModuleData: NeptuneModuleData
    get() = service<NeptuneModuleData>()

val PsiElement.neptuneModuleData: NeptuneModuleData?
    get() = ModuleUtil.findModuleForFile(containingFile)?.neptuneModuleData

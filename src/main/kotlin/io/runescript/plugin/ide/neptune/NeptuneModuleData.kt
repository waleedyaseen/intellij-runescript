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
        var dummy: Int = 0
    )

    val dummy: Int
        get() = state.dummy

    fun updateFromImportData(importData: NeptuneProjectImportData) = updateState {
        it.dummy = 0
        it
    }
}

val Module.neptuneModuleData: NeptuneModuleData
    get() = service<NeptuneModuleData>()

val PsiElement.neptuneModuleData: NeptuneModuleData?
    get() = ModuleUtil.findModuleForFile(containingFile)?.neptuneModuleData

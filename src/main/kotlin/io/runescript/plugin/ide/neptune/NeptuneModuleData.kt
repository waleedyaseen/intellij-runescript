package io.runescript.plugin.ide.neptune

import com.intellij.openapi.components.*
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtil
import com.intellij.psi.PsiElement
import com.intellij.util.xmlb.XmlSerializerUtil
import okio.withLock
import java.util.concurrent.locks.ReentrantLock

@State(
    name = "NeptuneModuleData",
    storages = [Storage(StoragePathMacros.MODULE_FILE)]
)
class NeptuneModuleData : PersistentStateComponent<NeptuneModuleData> {
    var arraysV2 = false
        private set

    override fun getState() = this

    override fun loadState(state: NeptuneModuleData) {
        XmlSerializerUtil.copyBean(state, this)
    }

    fun updateFromImportData(importData: NeptuneProjectImportData) {
        arraysV2 = importData.arraysV2
    }
}

val Module.neptuneModuleData: NeptuneModuleData?
    get() = service<NeptuneModuleData>()

val PsiElement.neptuneModuleData: NeptuneModuleData?
    get() = ModuleUtil.findModuleForFile(containingFile)?.neptuneModuleData

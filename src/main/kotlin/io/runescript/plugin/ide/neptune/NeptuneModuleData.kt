package io.runescript.plugin.ide.neptune

import com.intellij.openapi.components.*
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtil
import com.intellij.psi.PsiElement
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "NeptuneModuleData",
    storages = [Storage(StoragePathMacros.MODULE_FILE)]
)
class NeptuneModuleData : PersistentStateComponent<NeptuneModuleData> {

    override fun getState() = this

    override fun loadState(state: NeptuneModuleData) {
        XmlSerializerUtil.copyBean(state, this)
    }

    fun updateFromImportData(@Suppress("unused") importData: NeptuneProjectImportData) {

    }
}

val Module.neptuneModuleData: NeptuneModuleData?
    get() = service<NeptuneModuleData>()

val PsiElement.neptuneModuleData: NeptuneModuleData?
    get() = ModuleUtil.findModuleForFile(containingFile)?.neptuneModuleData
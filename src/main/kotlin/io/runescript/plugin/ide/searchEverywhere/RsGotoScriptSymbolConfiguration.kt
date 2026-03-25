package io.runescript.plugin.ide.searchEverywhere

import com.intellij.ide.util.gotoByName.ChooseByNameFilterConfiguration
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.StoragePathMacros
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
@State(name = "GotoScriptSymbolConfiguration", storages = [Storage(StoragePathMacros.WORKSPACE_FILE)])
class RsGotoScriptSymbolConfiguration : ChooseByNameFilterConfiguration<RsTriggerRef>() {
    companion object {
        @JvmStatic
        fun getInstance(project: Project): RsGotoScriptSymbolConfiguration = project.service()
    }

    override fun nameForElement(type: RsTriggerRef): String = type.displayName
}

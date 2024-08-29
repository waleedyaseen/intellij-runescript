package io.runescript.plugin.ide.searchEverywhere

import com.intellij.ide.util.gotoByName.ChooseByNameFilterConfiguration
import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
@State(name = "GotoScriptSymbolConfiguration", storages = [Storage(StoragePathMacros.WORKSPACE_FILE)])
class RsGotoScriptSymbolConfiguration : ChooseByNameFilterConfiguration<RsTriggerRef>() {

    companion object {
        @JvmStatic
        fun getInstance(project: Project): RsGotoScriptSymbolConfiguration = project.service()
    }

    override fun nameForElement(type: RsTriggerRef): String {
        return type.displayName
    }
}

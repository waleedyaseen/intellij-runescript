package io.runescript.plugin.ide.projectWizard

import com.intellij.icons.AllIcons
import com.intellij.openapi.module.ModuleType
import javax.swing.Icon

class RsModuleType : ModuleType<NeptuneModuleBuilder>(ID) {

    override fun createModuleBuilder(): NeptuneModuleBuilder {
        return NeptuneModuleBuilder()
    }

    override fun getName(): String {
        return "RuneScript Module"
    }

    override fun getDescription(): String {
        return "Empty module for developing content using RuneScript language."
    }

    override fun getNodeIcon(isOpened: Boolean): Icon {
        return AllIcons.Nodes.Module
    }

    companion object {
        const val ID = "RUNESCRIPT_MODULE"
        @JvmField
        val INSTANCE = RsModuleType()
    }
}
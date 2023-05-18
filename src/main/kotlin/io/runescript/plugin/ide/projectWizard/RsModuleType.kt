package io.runescript.plugin.ide.projectWizard

import com.intellij.icons.AllIcons
import com.intellij.openapi.module.ModuleType
import javax.swing.Icon

class RsModuleType : ModuleType<RsModuleBuilder>(ID) {

    override fun createModuleBuilder(): RsModuleBuilder {
        return RsModuleBuilder()
    }

    override fun getName(): String {
        return "RuneScript Module"
    }

    override fun getDescription(): String {
        return "Empty project for developing apps using RuneScript languages."
    }

    override fun getNodeIcon(isOpened: Boolean): Icon {
        return AllIcons.Nodes.Module
    }

    companion object {
        const val ID = "RUNESCRIPT_MODULE"
    }
}
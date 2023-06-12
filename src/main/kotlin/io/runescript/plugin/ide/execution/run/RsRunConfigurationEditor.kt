package io.runescript.plugin.ide.execution.run

import com.intellij.openapi.options.SettingsEditor
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

class RsRunConfigurationEditor() : SettingsEditor<RsRunConfiguration>() {


    override fun applyEditorTo(s: RsRunConfiguration) {

    }

    override fun resetEditorFrom(s: RsRunConfiguration) {

    }

    override fun createEditor(): JComponent {
        return panel {

        }
    }

}
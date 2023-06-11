package io.runescript.plugin.ide.configurable

import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.options.UiDslUnnamedConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.InputValidator
import com.intellij.openapi.ui.Messages
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBList
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.LabelPosition
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.ide.config.RsConfig
import javax.swing.DefaultListModel


class RsLanguageSettings(private val project: Project) : SearchableConfigurable, UiDslUnnamedConfigurable.Simple() {

    override fun Panel.createContent() {
        val itemListModel = DefaultListModel<String>()
        RsConfig.getPrimitiveTypes(project).let {
            itemListModel.addAll(it)
        }
        group("Type Settings") {
            row {
                val itemList = JBList(itemListModel)
                val component = ToolbarDecorator.createDecorator(itemList)
                    .setAddAction {
                        val literal = Messages.showInputDialog(
                            project,
                            RsBundle.message("settings.primitivetype.dialog.message"),
                            RsBundle.message("settings.primitivetype.dialog.title"),
                            Messages.getQuestionIcon(),
                            "",
                            LiteralInputValidator()
                        )
                        if (literal != null) {
                            if (itemListModel.contains(literal)) {
                                Messages.showMessageDialog(
                                    project,
                                    RsBundle.message("settings.primitivetype.dialog.error.exists"),
                                    RsBundle.message("settings.primitivetype.dialog.title"),
                                    Messages.getErrorIcon()
                                )
                            } else {
                                itemListModel.addElement(literal)
                            }
                        }
                    }
                    .setRemoveAction {
                        if (itemList.selectedIndex == -1) return@setRemoveAction
                        itemListModel.remove(itemList.selectedIndex)
                    }
                    .createPanel()
                cell(component)
                    .onApply {
                        val current = itemListModel.elements().toList()
                        if (RsConfig.getPrimitiveTypes(project) != current) {
                            RsConfig.setPrimitiveTypes(project, current)
                        }
                    }
                    .onReset {
                        itemListModel.clear()
                        RsConfig.getPrimitiveTypes(project).let {
                            itemListModel.addAll(it)
                        }
                    }
                    .align(Align.FILL)
                    .onIsModified {
                        val current = itemListModel.elements().toList()
                        return@onIsModified RsConfig.getPrimitiveTypes(project) != current
                    }
                    .label("Defined primitive types", LabelPosition.TOP)
            }
        }
    }

    override fun getDisplayName(): String {
        return "RuneScript Settings"
    }

    override fun getId(): String {
        return "runescript.languageSettings"
    }
}

internal class LiteralInputValidator : InputValidator {
    override fun checkInput(inputString: String?): Boolean {
        return !inputString.isNullOrBlank() && inputString.matches(Regex("[a-zA-Z0-9_]+"));
    }

    override fun canClose(inputString: String?): Boolean {
        return checkInput(inputString)
    }
}
package io.runescript.plugin.ide.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import io.runescript.plugin.ide.refactoring.RsExtractProcedureHandler
import io.runescript.plugin.lang.RuneScript

class RsExtractScriptAction : AnAction() {
    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.EDT

    override fun update(event: AnActionEvent) {
        val file = event.getData(CommonDataKeys.PSI_FILE)
        val editor = event.getData(CommonDataKeys.EDITOR)
        val isRuneScript = file?.language === RuneScript
        event.presentation.isVisible = isRuneScript
        event.presentation.isEnabled = isRuneScript && editor?.selectionModel?.hasSelection() == true
    }

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        val editor = event.getData(CommonDataKeys.EDITOR) ?: return
        val file = event.getData(CommonDataKeys.PSI_FILE) ?: return
        RsExtractProcedureHandler().invoke(project, editor, file, event.dataContext)
    }
}

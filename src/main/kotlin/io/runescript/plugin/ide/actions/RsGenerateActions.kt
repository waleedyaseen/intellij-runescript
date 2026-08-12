package io.runescript.plugin.ide.actions

import com.intellij.codeInsight.intention.BaseElementAtCaretIntentionAction
import com.intellij.codeInsight.intention.impl.ShowIntentionActionsHandler
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import io.runescript.plugin.ide.codeInsight.intention.controlFlow.RsAddSwitchCaseIntention
import io.runescript.plugin.ide.codeInsight.intention.controlFlow.RsAddSwitchDefaultIntention
import io.runescript.plugin.ide.codeInsight.intention.documentation.RsGenerateDocCommentIntention
import io.runescript.plugin.ide.codeInsight.intention.script.RsCreateScriptWrapperIntention

abstract class RsGenerateAction : AnAction() {
    protected abstract fun createIntention(): BaseElementAtCaretIntentionAction

    final override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    final override fun update(event: AnActionEvent) {
        val editor = event.getData(CommonDataKeys.EDITOR)
        val file = event.getData(CommonDataKeys.PSI_FILE)
        event.presentation.isEnabledAndVisible =
            editor != null && file != null && isAvailable(editor, file)
    }

    final override fun actionPerformed(event: AnActionEvent) {
        val editor = event.getData(CommonDataKeys.EDITOR) ?: return
        val file = event.getData(CommonDataKeys.PSI_FILE) ?: return
        val intention = createIntention()
        ShowIntentionActionsHandler.chooseActionAndInvoke(file, editor, intention, intention.text)
    }

    internal fun isAvailable(
        editor: Editor,
        file: PsiFile,
    ): Boolean {
        val element = file.findElementAt(editor.caretModel.offset) ?: return false
        return createIntention().isAvailable(file.project, editor, element)
    }
}

class RsGenerateDocAction : RsGenerateAction() {
    override fun createIntention(): BaseElementAtCaretIntentionAction = RsGenerateDocCommentIntention()
}

class RsGenerateScriptWrapperAction : RsGenerateAction() {
    override fun createIntention(): BaseElementAtCaretIntentionAction = RsCreateScriptWrapperIntention()
}

class RsGenerateSwitchCaseAction : RsGenerateAction() {
    override fun createIntention(): BaseElementAtCaretIntentionAction = RsAddSwitchCaseIntention()
}

class RsGenerateSwitchDefaultAction : RsGenerateAction() {
    override fun createIntention(): BaseElementAtCaretIntentionAction = RsAddSwitchDefaultIntention()
}

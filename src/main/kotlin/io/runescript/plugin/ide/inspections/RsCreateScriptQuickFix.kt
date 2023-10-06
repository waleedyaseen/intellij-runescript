package io.runescript.plugin.ide.inspections

import com.intellij.codeInsight.intention.preview.IntentionPreviewInfo
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.util.findParentOfType
import io.runescript.plugin.lang.psi.RsElementGenerator
import io.runescript.plugin.lang.psi.RsScript

class RsCreateScriptQuickFix(private val trigger: String, private val functionName: String) : LocalQuickFix {

    override fun getName(): String {
        return "Create script ('${functionName}')"
    }

    override fun getFamilyName(): String {
        return "Create script"
    }

    override fun generatePreview(project: Project, previewDescriptor: ProblemDescriptor): IntentionPreviewInfo =
        IntentionPreviewInfo.EMPTY

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val newScript = RsScriptBuilder(trigger, functionName)
            .statement("error(\"Not yet implemented\");")
            .build(project)
        val parentScript = descriptor.psiElement.findParentOfType<RsScript>()!!
        val parentFile = parentScript.parent
        parentFile.addAfter(newScript, parentScript)
        parentFile.addAfter(RsElementGenerator.createNewLine(project), parentScript)
        openAndSelect(project, newScript)
    }

    private fun openAndSelect(project: Project, script: RsScript) {
        val containingFile = script.containingFile.virtualFile ?: return
        val openDescriptor = OpenFileDescriptor(project, containingFile)
        val textEditor = FileEditorManager.getInstance(project).openTextEditor(openDescriptor, true) ?: return
        textEditor.selectionModel.removeSelection()
        val endOffset = script.textRange.endOffset
        textEditor.caretModel.moveToOffset(endOffset + 1)
        val manager = PsiDocumentManager.getInstance(project)
        manager.doPostponedOperationsAndUnblockDocument(textEditor.document)
        textEditor.scrollingModel.scrollToCaret(ScrollType.RELATIVE)
    }
}
package io.runescript.plugin.ide.inspections

import com.intellij.codeInspection.*
import com.intellij.openapi.editor.EditorModificationUtilEx
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.findParentOfType
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.lang.psi.RsHookFragment
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.RsVisitor
import io.runescript.plugin.lang.psi.isSourceFile

class RuneScriptUnresolvedClientscriptInspection : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : RsVisitor() {
            override fun visitHookFragment(o: RsHookFragment) {
                if (!o.isSourceFile()) return
                val resolvedClientScript = o.reference!!.resolve()
                if (resolvedClientScript == null) {
                    holder.registerProblem(o.nameLiteral,
                        RsBundle.message("inspection.error.unresolved.clientscript", o.nameLiteral.text),
                        ProblemHighlightType.LIKE_UNKNOWN_SYMBOL,
                        RsUnresolvedClientscriptQuickFix(o.nameLiteral.text)
                    )
                }
            }
        }
    }

    class RsUnresolvedClientscriptQuickFix(private val functionName: String) : LocalQuickFix {

        override fun getName(): String {
            return "Create clientscript ('${functionName}')"
        }

        override fun getFamilyName(): String {
            return "Create clientscript"
        }

        override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
            val newScript = RsScriptBuilder("clientscript", functionName)
                    .statement("error(\"Not yet implemented\");")
                    .build(project)
            val parentScript = descriptor.psiElement.findParentOfType<RsScript>()!!
            val parentFile = parentScript.parent!!
            parentFile.addAfter(newScript, parentScript)
            openAndSelect(project, newScript)
        }

        private fun openAndSelect(project: Project, script: RsScript) {
            val openDescriptor = OpenFileDescriptor(project, script.containingFile.virtualFile)
            val textEditor = FileEditorManager.getInstance(project).openTextEditor(openDescriptor, true)!!
            textEditor.selectionModel.removeSelection()
            val endOffset = script.textRange.endOffset
            textEditor.caretModel.moveToOffset(endOffset + 1)
            val manager = PsiDocumentManager.getInstance(project)
            manager.doPostponedOperationsAndUnblockDocument(textEditor.document)
            EditorModificationUtilEx.insertStringAtCaret(textEditor, "\n", false, false)
            textEditor.scrollingModel.scrollToCaret(ScrollType.RELATIVE)
        }
    }
}
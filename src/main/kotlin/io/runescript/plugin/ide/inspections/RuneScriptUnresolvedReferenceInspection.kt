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
import io.runescript.plugin.lang.psi.RsDynamicExpression
import io.runescript.plugin.lang.psi.RsGosubExpression
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.RsVisitor

class RuneScriptUnresolvedReferenceInspection : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : RsVisitor() {
            override fun visitDynamicExpression(o: RsDynamicExpression) {
                val resolvedGosub = o.reference!!.resolve()
                if (resolvedGosub == null) {
                    holder.registerProblem(o.nameLiteral,
                        RsBundle.message("inspection.error.unresolved.reference", o.nameLiteral.text),
                        ProblemHighlightType.LIKE_UNKNOWN_SYMBOL
                    )
                }
            }
        }
    }

}
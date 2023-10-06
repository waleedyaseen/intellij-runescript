package io.runescript.plugin.ide.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.lang.psi.RsGosubExpression
import io.runescript.plugin.lang.psi.RsVisitor
import io.runescript.plugin.lang.psi.isSourceFile

class RuneScriptUnresolvedProcedureInspection : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : RsVisitor() {
            override fun visitGosubExpression(o: RsGosubExpression) {
                if (!o.isSourceFile()) return
                val resolvedGosub = o.reference!!.resolve()
                if (resolvedGosub == null) {
                    holder.registerProblem(
                        o.nameLiteral,
                        RsBundle.message("inspection.error.unresolved.procedure", o.nameLiteral.text),
                        ProblemHighlightType.LIKE_UNKNOWN_SYMBOL,
                        RsCreateScriptQuickFix("proc", o.nameLiteral.text)
                    )
                }
            }
        }
    }
}
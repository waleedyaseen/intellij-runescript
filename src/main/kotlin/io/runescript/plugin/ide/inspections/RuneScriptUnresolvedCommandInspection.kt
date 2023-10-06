package io.runescript.plugin.ide.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.lang.psi.RsCommandExpression
import io.runescript.plugin.lang.psi.RsVisitor
import io.runescript.plugin.lang.psi.isSourceFile

class RuneScriptUnresolvedCommandInspection : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : RsVisitor() {
            override fun visitCommandExpression(o: RsCommandExpression) {
                if (!o.isSourceFile()) return
                val resolvedCommand = o.reference!!.resolve()
                if (resolvedCommand == null) {
                    holder.registerProblem(o.nameLiteral,
                            RsBundle.message("inspection.error.unresolved.command", o.nameLiteral.text),
                            ProblemHighlightType.LIKE_UNKNOWN_SYMBOL
                    )
                }
            }
        }
    }
}
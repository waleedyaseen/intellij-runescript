package io.runescript.plugin.ide.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.lang.psi.RsScopedVariableExpression
import io.runescript.plugin.lang.psi.RsVisitor
import io.runescript.plugin.lang.psi.isSourceFile

class RuneScriptUnresolvedScopedVariableInspection : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : RsVisitor() {
            override fun visitScopedVariableExpression(o: RsScopedVariableExpression) {
                if (!o.isSourceFile()) return
                val reference = o.reference ?: return
                if (reference.resolve() == null) {
                    holder.registerProblem(o,
                            RsBundle.message("inspection.error.unresolved.scoped.variable", o.nameLiteral.text),
                            ProblemHighlightType.LIKE_UNKNOWN_SYMBOL
                    )
                }
            }
        }
    }
}
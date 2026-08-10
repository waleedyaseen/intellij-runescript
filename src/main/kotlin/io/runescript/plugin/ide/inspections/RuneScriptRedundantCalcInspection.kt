package io.runescript.plugin.ide.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.ide.inspections.fixes.RsRemoveRedundantCalcQuickFix
import io.runescript.plugin.lang.psi.RsArithmeticExpression
import io.runescript.plugin.lang.psi.RsCalcExpression
import io.runescript.plugin.lang.psi.RsVisitor

class RuneScriptRedundantCalcInspection : LocalInspectionTool() {
    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
    ): PsiElementVisitor =
        object : RsVisitor() {
            override fun visitCalcExpression(o: RsCalcExpression) {
                if (o.expression is RsArithmeticExpression) return
                holder.registerProblem(
                    o.calc,
                    RsBundle.message("inspection.weak.warning.redundant.calc"),
                    ProblemHighlightType.LIKE_UNUSED_SYMBOL,
                    RsRemoveRedundantCalcQuickFix(),
                )
            }
        }
}

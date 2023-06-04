package io.runescript.plugin.ide.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.lang.psi.RsLocalVariableExpression
import io.runescript.plugin.lang.psi.RsVisitor

class RsUnresolvedLocalVariableInspection : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : RsVisitor() {
            override fun visitLocalVariableExpression(o: RsLocalVariableExpression) {
                val reference = o.reference ?: return
                if (reference.resolve() == null) {
                    holder.registerProblem(o,
                            RsBundle.message("inspection.error.unresolved.local.variable", o.nameLiteral.text),
                            ProblemHighlightType.LIKE_UNKNOWN_SYMBOL
                    )
                }
            }
        }
    }
}
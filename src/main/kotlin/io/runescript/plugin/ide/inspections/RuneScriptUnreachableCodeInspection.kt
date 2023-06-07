package io.runescript.plugin.ide.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.lang.psi.RsStatement
import io.runescript.plugin.lang.psi.RsVisitor
import io.runescript.plugin.lang.psi.controlFlowHolder

class RuneScriptUnreachableCodeInspection : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : RsVisitor() {
            override fun visitStatement(element: RsStatement) {
                element.controlFlowHolder?.controlFlow?.let {
                    val insn = it.instructions.find { instruction -> instruction.element == element } ?: return@let
                    if (it.isReachable(insn)) {
                        return@visitStatement
                    }
                    holder.registerProblem(element, RsBundle.message("inspection.warning.unreachable.code"), ProblemHighlightType.WARNING)
                    super.visitStatement(element)
                }
            }
        }
    }
}
package io.runescript.plugin.ide.inspections

import com.intellij.codeInsight.controlflow.ControlFlowUtil
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElementVisitor
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.lang.psi.RsStatement
import io.runescript.plugin.lang.psi.RsVisitor
import io.runescript.plugin.lang.psi.controlFlowHolder

class RsUnreachableCodeInspection : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : RsVisitor() {
            override fun visitStatement(element: RsStatement) {
                super.visitStatement(element)
                element.controlFlowHolder?.let {
                    val targetInstruction = it.controlFlow.instructions.find { instruction -> instruction.element == element }
                            ?: return@let
                    val reached = Ref<Boolean>(false)
                    ControlFlowUtil.process(it.controlFlow.instructions, 0) { instruction ->
                        if (instruction == targetInstruction) {
                            reached.set(true)
                            return@process false
                        }
                        return@process true
                    }
                    if (!reached.get()) {
                        holder.registerProblem(element,
                                RsBundle.message("inspection.warning.unreachable.code"),
                                ProblemHighlightType.WARNING)
                    }
                }
            }
        }
    }
}
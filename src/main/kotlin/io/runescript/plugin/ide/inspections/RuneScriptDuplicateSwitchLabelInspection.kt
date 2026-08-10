package io.runescript.plugin.ide.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import io.runescript.plugin.ide.inspections.fixes.RsMergeDuplicateSwitchCasesQuickFix
import io.runescript.plugin.ide.inspections.fixes.RsRemoveDuplicateSwitchLabelQuickFix
import io.runescript.plugin.lang.psi.RsExpression
import io.runescript.plugin.lang.psi.RsSwitchCase
import io.runescript.plugin.lang.psi.RsSwitchStatement
import io.runescript.plugin.lang.psi.RsVisitor

class RuneScriptDuplicateSwitchLabelInspection : LocalInspectionTool() {
    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
    ): PsiElementVisitor =
        object : RsVisitor() {
            override fun visitSwitchStatement(o: RsSwitchStatement) {
                val seen = mutableMapOf<Any, RsSwitchCase>()
                for (case in o.switchCaseList) {
                    for (expression in case.expressionList) {
                        val key = expression.switchLabelKey() ?: continue
                        val original = seen.putIfAbsent(key, case) ?: continue
                        val fixes =
                            buildList {
                                add(RsRemoveDuplicateSwitchLabelQuickFix(expression.text))
                                if (original != case && original.statementList.text.trim() == case.statementList.text.trim()) {
                                    add(RsMergeDuplicateSwitchCasesQuickFix(expression.text))
                                }
                            }.toTypedArray()
                        holder.registerProblem(expression, "Duplicate switch label '${expression.text}'", *fixes)
                    }
                }
            }
        }
}

package io.runescript.plugin.ide.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.ide.inspections.fixes.RsRemoveRedundantElseQuickFix
import io.runescript.plugin.lang.psi.RsBlockStatement
import io.runescript.plugin.lang.psi.RsIfStatement
import io.runescript.plugin.lang.psi.RsReturnStatement
import io.runescript.plugin.lang.psi.RsStatement
import io.runescript.plugin.lang.psi.RsStatementList
import io.runescript.plugin.lang.psi.RsSwitchCaseDefaultExpression
import io.runescript.plugin.lang.psi.RsSwitchStatement
import io.runescript.plugin.lang.psi.RsVisitor

class RuneScriptRedundantElseInspection : LocalInspectionTool() {
    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
    ): PsiElementVisitor =
        object : RsVisitor() {
            override fun visitIfStatement(o: RsIfStatement) {
                val elseKeyword = o.getElse() ?: return
                if (o.parent !is RsStatementList || o.falseStatement == null || !o.trueStatement.alwaysExits()) return
                holder.registerProblem(
                    elseKeyword,
                    RsBundle.message("inspection.weak.warning.redundant.else"),
                    ProblemHighlightType.LIKE_UNUSED_SYMBOL,
                    RsRemoveRedundantElseQuickFix(),
                )
            }
        }
}

private fun RsStatement?.alwaysExits(): Boolean =
    when (this) {
        is RsReturnStatement -> {
            true
        }

        is RsBlockStatement -> {
            statementList.statementList.any(RsStatement::alwaysExits)
        }

        is RsIfStatement -> {
            trueStatement.alwaysExits() && falseStatement.alwaysExits()
        }

        is RsSwitchStatement -> {
            switchCaseList.isNotEmpty() &&
                switchCaseList.any { switchCase ->
                    switchCase.expressionList.any { it is RsSwitchCaseDefaultExpression }
                } &&
                switchCaseList.all { switchCase ->
                    switchCase.statementList.statementList.any(RsStatement::alwaysExits)
                }
        }

        else -> {
            false
        }
    }

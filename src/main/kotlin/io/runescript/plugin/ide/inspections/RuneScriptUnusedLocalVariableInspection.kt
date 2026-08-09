package io.runescript.plugin.ide.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.ide.inspections.fixes.RsDeleteLocalVariableFix
import io.runescript.plugin.lang.psi.RsLocalVariableDeclarationStatement
import io.runescript.plugin.lang.psi.RsLocalVariableExpression
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.RsVisitor

class RuneScriptUnusedLocalVariableInspection : LocalInspectionTool() {
    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
    ): PsiElementVisitor {
        return object : RsVisitor() {
            override fun visitScript(o: RsScript) {
                val declarations =
                    PsiTreeUtil
                        .findChildrenOfType(o, RsLocalVariableDeclarationStatement::class.java)
                        .mapNotNull { statement ->
                            val variable = statement.expressionList.firstOrNull() as? RsLocalVariableExpression
                            variable?.let { it to statement }
                        }.toMap()
                if (declarations.isEmpty()) return

                val usedDeclarations = hashSetOf<RsLocalVariableExpression>()
                for (expression in PsiTreeUtil.findChildrenOfType(o, RsLocalVariableExpression::class.java)) {
                    if (expression in declarations) continue
                    val declaration = expression.reference?.resolve() as? RsLocalVariableExpression ?: continue
                    if (declaration in declarations) {
                        usedDeclarations += declaration
                    }
                }

                for ((variable, statement) in declarations) {
                    if (variable !in usedDeclarations) {
                        holder.registerProblem(
                            statement,
                            "Unused local variable",
                            ProblemHighlightType.LIKE_UNUSED_SYMBOL,
                            RsDeleteLocalVariableFix(),
                        )
                    }
                }
            }
        }
    }
}

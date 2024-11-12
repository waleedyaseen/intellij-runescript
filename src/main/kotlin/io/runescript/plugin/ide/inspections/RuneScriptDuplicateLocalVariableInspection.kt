package io.runescript.plugin.ide.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.ResolveState
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.lang.psi.RsArrayVariableDeclarationStatement
import io.runescript.plugin.lang.psi.RsLocalVariableDeclarationStatement
import io.runescript.plugin.lang.psi.RsLocalVariableExpression
import io.runescript.plugin.lang.psi.RsParameter
import io.runescript.plugin.lang.psi.RsVisitor
import io.runescript.plugin.lang.psi.scope.RsLocalVariableResolver
import io.runescript.plugin.lang.psi.scope.RsResolveMode
import io.runescript.plugin.lang.psi.scope.RsScopesUtil

class RuneScriptDuplicateLocalVariableInspection : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : RsVisitor() {

            override fun visitParameter(o: RsParameter) {
                val name = o.localVariableExpression.name ?: return
                var prev = o.prevSibling
                while (prev != null) {
                    if (prev is RsParameter) {
                        val prevName = prev.localVariableExpression.name
                        if (name == prevName) {
                            holder.registerProblem(
                                o.localVariableExpression,
                                RsBundle.message("inspection.error.duplicate.parameter", name),
                                ProblemHighlightType.GENERIC_ERROR
                            )
                            break
                        }
                    }
                    prev = prev.prevSibling
                }
            }

            override fun visitLocalVariableDeclarationStatement(o: RsLocalVariableDeclarationStatement) {
                val prev = o.prevSibling ?: o.parent ?: return
                val expr = o.expressionList[0] as RsLocalVariableExpression
                val name = expr.name ?: return
                val resolver = RsLocalVariableResolver(name, RsResolveMode.Variables)
                RsScopesUtil.walkUpScopes(resolver, ResolveState.initial(), prev)
                if (resolver.declaration != null) {
                    holder.registerProblem(
                        expr,
                        RsBundle.message("inspection.error.duplicate.local.variable", name),
                        ProblemHighlightType.GENERIC_ERROR
                    )
                }
            }

            override fun visitArrayVariableDeclarationStatement(o: RsArrayVariableDeclarationStatement) {
                val prev = o.prevSibling ?: o.parent ?: return
                val expr = o.expressionList[0] as RsLocalVariableExpression
                val name = expr.name ?: return
                val resolver = RsLocalVariableResolver(name, RsResolveMode.Arrays)
                RsScopesUtil.walkUpScopes(resolver, ResolveState.initial(), prev)
                if (resolver.declaration != null) {
                    holder.registerProblem(
                        expr,
                        RsBundle.message("inspection.error.duplicate.array.variable", name),
                        ProblemHighlightType.GENERIC_ERROR
                    )
                }
            }
        }
    }
}
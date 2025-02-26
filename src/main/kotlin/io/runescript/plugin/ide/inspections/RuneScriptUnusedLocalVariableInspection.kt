package io.runescript.plugin.ide.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.searches.ReferencesSearch
import io.runescript.plugin.ide.inspections.fixes.RsDeleteLocalVariableFix
import io.runescript.plugin.lang.psi.RsLocalVariableDeclarationStatement
import io.runescript.plugin.lang.psi.RsVisitor

class RuneScriptUnusedLocalVariableInspection : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : RsVisitor() {
            override fun visitLocalVariableDeclarationStatement(o: RsLocalVariableDeclarationStatement) {
                val expr = o.expressionList.getOrNull(0) ?: return
                val references = ReferencesSearch.search(expr, GlobalSearchScope.fileScope(expr.containingFile)).findAll()
                if (references.isEmpty()) {
                    holder.registerProblem(o,
                            "Unused local variable",
                            ProblemHighlightType.LIKE_UNUSED_SYMBOL,
                            RsDeleteLocalVariableFix()
                    )
                }
            }
        }
    }
}
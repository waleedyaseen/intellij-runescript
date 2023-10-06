package io.runescript.plugin.ide.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.lang.psi.RsHookFragment
import io.runescript.plugin.lang.psi.RsVisitor
import io.runescript.plugin.lang.psi.isSourceFile

class RuneScriptUnresolvedClientscriptInspection : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : RsVisitor() {
            override fun visitHookFragment(o: RsHookFragment) {
                if (!o.isSourceFile()) return
                val resolvedClientScript = o.reference!!.resolve()
                if (resolvedClientScript == null) {
                    holder.registerProblem(
                        o.nameLiteral,
                        RsBundle.message("inspection.error.unresolved.clientscript", o.nameLiteral.text),
                        ProblemHighlightType.LIKE_UNKNOWN_SYMBOL
                    )
                }
            }
        }
    }
}
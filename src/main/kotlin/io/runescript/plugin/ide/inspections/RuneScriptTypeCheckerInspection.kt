package io.runescript.plugin.ide.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import io.runescript.plugin.lang.psi.RsHookFragment
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.RsVisitor
import io.runescript.plugin.lang.psi.isSourceFile
import io.runescript.plugin.lang.psi.typechecker.RsInferenceDataHolder
import io.runescript.plugin.lang.psi.typechecker.TypeCheckingUtil

class RuneScriptTypeCheckerInspection : LocalInspectionTool() {
    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
    ): PsiElementVisitor {
        return object : RsVisitor() {
            override fun visitScript(o: RsScript) {
                inspect(o)
            }

            override fun visitHookFragment(o: RsHookFragment) {
                inspect(o)
            }

            private fun inspect(root: RsInferenceDataHolder) {
                if (!root.isSourceFile()) return
                for (diagnostic in TypeCheckingUtil.typeCheck(root)) {
                    if (!diagnostic.isError()) continue
                    val message = diagnostic.message.format(*diagnostic.messageArgs.toTypedArray())
                    holder.registerProblem(
                        diagnostic.element,
                        message,
                    )
                }
            }
        }
    }
}

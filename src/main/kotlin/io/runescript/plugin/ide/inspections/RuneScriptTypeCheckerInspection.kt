package io.runescript.plugin.ide.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import io.runescript.plugin.lang.psi.RsVisitor
import io.runescript.plugin.lang.psi.isSourceFile
import io.runescript.plugin.lang.psi.typechecker.TypeCheckingUtil

class RuneScriptTypeCheckerInspection : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : RsVisitor() {

            override fun visitElement(element: PsiElement) {
                if (!element.isSourceFile()) return
                val diagnostics = TypeCheckingUtil.getErrors(element)
                if (diagnostics.isEmpty()) return
                for (diagnostic in diagnostics) {
                    val message = diagnostic.message.format(*diagnostic.messageArgs.toTypedArray())
                    holder.registerProblem(
                        diagnostic.element,
                        message
                    )
                }
            }
        }
    }
}
package io.runescript.plugin.ide.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import io.runescript.plugin.lang.psi.RsVisitor
import io.runescript.plugin.lang.psi.type.typeErrors

class RuneScriptTypeCheckerInspection : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : RsVisitor() {

            override fun visitElement(element: PsiElement) {
                element.typeErrors.forEach {
                    holder.registerProblem(it.element, it.message)
                }
            }
        }
    }
}
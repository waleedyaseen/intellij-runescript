package io.runescript.plugin.ide.inspections.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import io.runescript.plugin.ide.codeInsight.isSafeToDiscard
import io.runescript.plugin.lang.psi.RsElementGenerator
import io.runescript.plugin.lang.psi.RsLocalVariableDeclarationStatement

class RsDeleteLocalVariableFix : LocalQuickFix {
    override fun getFamilyName(): String = "Delete local variable"

    override fun applyFix(
        project: Project,
        previewDescriptor: ProblemDescriptor,
    ) {
        if (previewDescriptor.psiElement is RsLocalVariableDeclarationStatement) {
            val decl = previewDescriptor.psiElement as RsLocalVariableDeclarationStatement
            val expr = decl.expressionList.getOrNull(1)
            if (expr == null || expr.isSafeToDiscard()) {
                decl.delete()
            } else {
                val stmt = RsElementGenerator.createExpressionStatement(project, expr.text)
                decl.replace(stmt)
            }
        }
    }
}

package io.runescript.plugin.ide.inspections.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import io.runescript.plugin.ide.refactoring.RsLocalVariableDeletion
import io.runescript.plugin.lang.psi.RsLocalVariableDeclarationStatement

class RsDeleteLocalVariableFix : LocalQuickFix {
    override fun getFamilyName(): String = "Delete local variable"

    override fun applyFix(
        project: Project,
        previewDescriptor: ProblemDescriptor,
    ) {
        if (previewDescriptor.psiElement is RsLocalVariableDeclarationStatement) {
            val decl = previewDescriptor.psiElement as RsLocalVariableDeclarationStatement
            RsLocalVariableDeletion.deletePreservingEffects(project, decl)
        }
    }
}

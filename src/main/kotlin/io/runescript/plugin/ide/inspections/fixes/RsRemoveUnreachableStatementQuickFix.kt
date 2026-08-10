package io.runescript.plugin.ide.inspections.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import io.runescript.plugin.lang.psi.RsStatement

class RsRemoveUnreachableStatementQuickFix : LocalQuickFix {
    override fun getFamilyName(): String = "Remove unreachable statement"

    override fun applyFix(
        project: Project,
        descriptor: ProblemDescriptor,
    ) {
        (descriptor.psiElement as? RsStatement)?.delete()
    }
}

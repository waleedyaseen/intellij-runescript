package io.runescript.plugin.ide.inspections.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.util.parentOfType
import io.runescript.plugin.lang.psi.RsCalcExpression

class RsRemoveRedundantCalcQuickFix : LocalQuickFix {
    override fun getFamilyName(): String = "Remove redundant 'calc'"

    override fun applyFix(
        project: Project,
        descriptor: ProblemDescriptor,
    ) {
        val calc = descriptor.psiElement.parentOfType<RsCalcExpression>() ?: return
        val document = PsiDocumentManager.getInstance(project).getDocument(calc.containingFile) ?: return
        document.deleteString(calc.rparen.textRange.startOffset, calc.rparen.textRange.endOffset)
        document.deleteString(calc.calc.textRange.startOffset, calc.lparen.textRange.endOffset)
        PsiDocumentManager.getInstance(project).commitDocument(document)
    }
}

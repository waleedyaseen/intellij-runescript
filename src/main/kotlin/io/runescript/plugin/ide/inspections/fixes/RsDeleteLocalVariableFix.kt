package io.runescript.plugin.ide.inspections.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import io.runescript.plugin.lang.psi.*
import io.runescript.plugin.symbollang.psi.RsSymSymbol

class RsDeleteLocalVariableFix : LocalQuickFix {
    override fun getFamilyName(): String = "Delete local variable"

    override fun applyFix(project: Project, previewDescriptor: ProblemDescriptor) {
        if (previewDescriptor.psiElement is RsLocalVariableDeclarationStatement) {
            val decl = previewDescriptor.psiElement as RsLocalVariableDeclarationStatement
            val expr = decl.expressionList.getOrNull(1)
            if (expr == null || expr.isSimpleToRemove()) {
                decl.delete()
            } else {
                val stmt = RsElementGenerator.createExpressionStatement(project, expr.text)
                decl.replace(stmt)
            }
        }
    }
}

private fun RsExpression.isSimpleToRemove(): Boolean {
    if (this is RsStringLiteralExpression) {
       return stringLiteralContent.stringInterpolationExpressionList.all { it.isSimpleToRemove() }
    }
    return when (this) {
        is RsNullLiteralExpression,
        is RsIntegerLiteralExpression,
        is RsCoordLiteralExpression,
        is RsBooleanLiteralExpression,
        is RsLongLiteralExpression,
        is RsLocalVariableExpression,
        is RsConstantExpression,
        is RsArrayAccessExpression -> true
        is RsStringInterpolationExpression -> expression.isSimpleToRemove()
        is RsParExpression -> expression.isSimpleToRemove()
        is RsCalcExpression -> expression.isSimpleToRemove()
        is RsBinaryExpression -> left.isSimpleToRemove() && right.isSimpleToRemove()
        is RsDynamicExpression -> {
            val reference = reference?.resolve() ?: return false
            return reference is RsSymSymbol
        }

        else -> false
    }
}
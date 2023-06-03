package io.runescript.plugin.ide.codeInsight.intention.controlFlow

import com.intellij.codeInsight.intention.BaseElementAtCaretIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.lang.psi.*

class RsMergeIfAndIntention : BaseElementAtCaretIntentionAction() {

    override fun getFamilyName(): String {
        return RsBundle.message("intention.merge.if.and.family.name")
    }

    override fun getText(): String {
        return RsBundle.message("intention.merge.if.and.name")
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        val ifStmt = element.parent as RsIfStatement
        val trueStmt = ifStmt.trueStatement.toSingleStatement() as RsIfStatement
        val leftParen = ifStmt.expression.precedence > PRECEDENCE_LOGICAL_AND
        val rightParen = trueStmt.expression.precedence > PRECEDENCE_LOGICAL_AND
        val exprText = buildString {
            if (leftParen) {
                append('(')
            }
            append(ifStmt.expression.text)
            if (leftParen) {
                append(')')
            }
            append('&')
            if (rightParen) {
                append('(')
            }
            append(trueStmt.expression.text)
            if (rightParen) {
                append(')')
            }
        }
        val conditionExpr = RsElementGenerator.createConditionExpression(element.project, exprText)
        ifStmt.expression.replace(conditionExpr)
        ifStmt.trueStatement.replace(trueStmt.trueStatement)
    }

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        val ifStmt = element.parent
        if (ifStmt !is RsIfStatement) {
            return false
        }
        val trueStmt = ifStmt.trueStatement.toSingleStatement()
        if (trueStmt !is RsIfStatement) {
            return false
        }
        return trueStmt.falseStatement.isNullOrEmpty()
    }
}

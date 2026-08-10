package io.runescript.plugin.ide.codeInsight.intention.controlFlow

import com.intellij.codeInsight.intention.BaseElementAtCaretIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleManager
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.lang.psi.RsConditionExpression
import io.runescript.plugin.lang.psi.RsElementGenerator
import io.runescript.plugin.lang.psi.RsExpression
import io.runescript.plugin.lang.psi.RsIfStatement
import io.runescript.plugin.lang.psi.RsParExpression
import io.runescript.plugin.lang.psi.RsRelationalValueExpression
import io.runescript.plugin.lang.psi.isLogicalAnd

class RsSplitIfAndIntention : BaseElementAtCaretIntentionAction() {
    override fun getFamilyName(): String = RsBundle.message("intention.split.if.and.family.name")

    override fun getText(): String = RsBundle.message("intention.split.if.and.name")

    override fun invoke(
        project: Project,
        editor: Editor,
        element: PsiElement,
    ) {
        val (ifStatement, condition) = findCondition(element) ?: return
        val right = condition.right ?: return
        val body = ifStatement.trueStatement ?: return
        val replacement =
            RsElementGenerator.createStatement(
                project,
                "if (${condition.left.text}) {\nif (${right.text}) ${body.text}\n}",
            )
        CodeStyleManager.getInstance(project).reformat(ifStatement.replace(replacement))
    }

    override fun isAvailable(
        project: Project,
        editor: Editor,
        element: PsiElement,
    ): Boolean {
        val (ifStatement, condition) = findCondition(element) ?: return false
        if (ifStatement.falseStatement != null || ifStatement.trueStatement == null) return false
        return condition.conditionOp.isLogicalAnd() && condition.right != null
    }

    private fun findCondition(element: PsiElement): Pair<RsIfStatement, RsConditionExpression>? {
        val ifStatement = element.parent as? RsIfStatement ?: return null
        if (element != ifStatement.`if`) return null
        val condition = ifStatement.expression?.unwrapGrouping() as? RsConditionExpression ?: return null
        return ifStatement to condition
    }

    private tailrec fun RsExpression.unwrapGrouping(): RsExpression =
        when (this) {
            is RsParExpression -> expression.unwrapGrouping()
            is RsRelationalValueExpression -> expression?.unwrapGrouping() ?: this
            else -> this
        }
}

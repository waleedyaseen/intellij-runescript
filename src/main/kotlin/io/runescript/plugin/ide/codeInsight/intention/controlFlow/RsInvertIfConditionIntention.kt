package io.runescript.plugin.ide.codeInsight.intention.controlFlow

import com.intellij.codeInsight.intention.BaseElementAtCaretIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleManager
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.lang.psi.RsBlockStatement
import io.runescript.plugin.lang.psi.RsElementGenerator
import io.runescript.plugin.lang.psi.RsIfStatement
import io.runescript.plugin.lang.psi.RsStatement

class RsInvertIfConditionIntention : BaseElementAtCaretIntentionAction() {
    override fun getFamilyName(): String = RsBundle.message("intention.invert.if.condition.family.name")

    override fun getText(): String = RsBundle.message("intention.invert.if.condition.name")

    override fun invoke(
        project: Project,
        editor: Editor,
        element: PsiElement,
    ) {
        val ifStatement = findIf(element) ?: return
        val condition = ifStatement.expression ?: return
        val negatedCondition = RsConditionNegator.negate(condition) ?: return
        val trueBranch = ifStatement.trueStatement ?: return
        val falseBranch = ifStatement.falseStatement ?: return
        val replacement =
            RsElementGenerator.createStatement(
                project,
                "if ($negatedCondition) {\n${falseBranch.bodyText()}\n} else {\n${trueBranch.bodyText()}\n}",
            )
        CodeStyleManager.getInstance(project).reformat(ifStatement.replace(replacement))
    }

    override fun isAvailable(
        project: Project,
        editor: Editor,
        element: PsiElement,
    ): Boolean {
        val ifStatement = findIf(element) ?: return false
        val condition = ifStatement.expression ?: return false
        return ifStatement.trueStatement != null &&
            ifStatement.falseStatement != null &&
            RsConditionNegator.negate(condition) != null
    }

    private fun RsStatement.bodyText(): String = if (this is RsBlockStatement) statementList.text.trim() else text

    private fun findIf(element: PsiElement): RsIfStatement? {
        val ifStatement = element.parent as? RsIfStatement ?: return null
        return ifStatement.takeIf { element == it.`if` }
    }
}

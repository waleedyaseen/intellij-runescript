package io.runescript.plugin.ide.codeInsight.intention.controlFlow

import com.intellij.codeInsight.intention.BaseElementAtCaretIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.lang.psi.RsBlockStatement
import io.runescript.plugin.lang.psi.RsElementGenerator
import io.runescript.plugin.lang.psi.RsIfStatement
import io.runescript.plugin.lang.psi.RsStatement

class RsMergeEquivalentElseIfIntention : BaseElementAtCaretIntentionAction() {
    override fun getFamilyName(): String = RsBundle.message("intention.merge.equivalent.else.if.family.name")

    override fun getText(): String = RsBundle.message("intention.merge.equivalent.else.if.name")

    override fun invoke(
        project: Project,
        editor: Editor,
        element: PsiElement,
    ) {
        val branches = findBranches(element) ?: return
        val tail =
            branches.second.falseStatement
                ?.let { " else ${it.text}" }
                .orEmpty()
        val replacement =
            RsElementGenerator.createStatement(
                project,
                "if ((${branches.first.expression?.text}) | (${branches.second.expression?.text})) {\n" +
                    "${branches.first.trueStatement?.bodyText()}\n}$tail",
            )
        CodeStyleManager.getInstance(project).reformat(branches.first.replace(replacement))
    }

    override fun isAvailable(
        project: Project,
        editor: Editor,
        element: PsiElement,
    ): Boolean = findBranches(element) != null

    private fun findBranches(element: PsiElement): Pair<RsIfStatement, RsIfStatement>? {
        val first = element.parent as? RsIfStatement ?: return null
        if (element != first.`if` || first.expression == null) return null
        val second = first.falseStatement as? RsIfStatement ?: return null
        if (second.expression == null) return null
        val firstBody = first.trueStatement?.bodyText() ?: return null
        val secondBody = second.trueStatement?.bodyText() ?: return null
        if (firstBody != secondBody) return null
        if (first.hasUnpreservedComments(second)) return null
        return first to second
    }

    private fun RsIfStatement.hasUnpreservedComments(second: RsIfStatement): Boolean {
        val preserved = listOfNotNull(expression, trueStatement, second.expression, second.trueStatement, second.falseStatement)
        return PsiTreeUtil.findChildrenOfType(this, PsiComment::class.java).any { comment ->
            preserved.none { it.textRange.contains(comment.textRange) }
        }
    }

    private fun RsStatement.bodyText(): String = (if (this is RsBlockStatement) statementList.text else text).trim()
}

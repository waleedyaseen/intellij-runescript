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
import io.runescript.plugin.lang.psi.RsWhileStatement

class RsAddBracesIntention : BaseElementAtCaretIntentionAction() {
    override fun getFamilyName(): String = RsBundle.message("intention.add.braces.family.name")

    override fun getText(): String = RsBundle.message("intention.add.braces.name")

    override fun invoke(
        project: Project,
        editor: Editor,
        element: PsiElement,
    ) {
        val statement = findStatement(element) ?: return
        val block = RsElementGenerator.createStatement(project, "{\n${statement.text}\n}") as RsBlockStatement
        CodeStyleManager.getInstance(project).reformat(statement.replace(block))
    }

    override fun isAvailable(
        project: Project,
        editor: Editor,
        element: PsiElement,
    ): Boolean = findStatement(element) != null

    private fun findStatement(element: PsiElement): RsStatement? {
        val controlFlow = element.parent
        val statement =
            when {
                controlFlow is RsIfStatement && element == controlFlow.`if` -> controlFlow.trueStatement
                controlFlow is RsIfStatement && element == controlFlow.`else` -> controlFlow.falseStatement
                controlFlow is RsWhileStatement && element == controlFlow.`while` -> controlFlow.statement
                else -> null
            }
        return statement?.takeUnless { it is RsBlockStatement }
    }
}

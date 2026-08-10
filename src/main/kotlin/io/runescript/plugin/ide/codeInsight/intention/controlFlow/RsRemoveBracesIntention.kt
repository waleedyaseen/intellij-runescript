package io.runescript.plugin.ide.codeInsight.intention.controlFlow

import com.intellij.codeInsight.intention.BaseElementAtCaretIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.lang.psi.RsArrayVariableDeclarationStatement
import io.runescript.plugin.lang.psi.RsBlockStatement
import io.runescript.plugin.lang.psi.RsIfStatement
import io.runescript.plugin.lang.psi.RsLocalVariableDeclarationStatement
import io.runescript.plugin.lang.psi.RsStatement
import io.runescript.plugin.lang.psi.RsWhileStatement

class RsRemoveBracesIntention : BaseElementAtCaretIntentionAction() {
    override fun getFamilyName(): String = RsBundle.message("intention.remove.braces.family.name")

    override fun getText(): String = RsBundle.message("intention.remove.braces.name")

    override fun invoke(
        project: Project,
        editor: Editor,
        element: PsiElement,
    ) {
        val (block, statement) = findReplacement(element) ?: return
        val controlFlow = block.parent
        block.replace(statement)
        val codeStyleManager = CodeStyleManager.getInstance(project)
        val reformatted = codeStyleManager.reformat(controlFlow)
        if (reformatted is RsIfStatement) {
            PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.document)
            reformatted.`else`?.let { codeStyleManager.adjustLineIndent(reformatted.containingFile, it.textOffset) }
        }
    }

    override fun isAvailable(
        project: Project,
        editor: Editor,
        element: PsiElement,
    ): Boolean = findReplacement(element) != null

    private fun findReplacement(element: PsiElement): Pair<RsBlockStatement, RsStatement>? {
        val block = element.parent as? RsBlockStatement ?: return null
        if (element != block.lbrace && element != block.rbrace) return null
        if (PsiTreeUtil.findChildOfType(block, PsiComment::class.java) != null) return null
        val statement = block.statementList.statementList.singleOrNull() ?: return null
        if (statement is RsLocalVariableDeclarationStatement || statement is RsArrayVariableDeclarationStatement) {
            return null
        }

        when (val controlFlow = block.parent) {
            is RsIfStatement -> {
                if (block != controlFlow.trueStatement && block != controlFlow.falseStatement) return null
                if (block == controlFlow.trueStatement && controlFlow.falseStatement != null &&
                    statement is RsIfStatement && statement.falseStatement == null
                ) {
                    return null
                }
            }

            is RsWhileStatement -> {
                if (block != controlFlow.statement) return null
            }

            else -> {
                return null
            }
        }
        return block to statement
    }
}

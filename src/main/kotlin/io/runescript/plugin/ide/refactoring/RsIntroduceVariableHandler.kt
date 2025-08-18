package io.runescript.plugin.ide.refactoring

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.refactoring.rename.inplace.VariableInplaceRenamer
import com.intellij.refactoring.util.CommonRefactoringUtil
import io.runescript.plugin.lang.psi.*
import io.runescript.plugin.lang.psi.type.*

class RsIntroduceVariableHandler : RsRefactoringActionBase() {

    override fun invoke(
        project: Project,
        editor: Editor,
        file: PsiFile,
        dataContext: DataContext
    ) {
        if (file !is RsFile || !CommonRefactoringUtil.checkReadOnlyStatus(file)) return
        PsiDocumentManager.getInstance(project).commitDocument(editor.document)
        val expression = findTargetExpression(file, editor) ?: return
        val parent = PsiTreeUtil.getParentOfType(expression, RsStatement::class.java) ?: return
        val parentBlock = PsiTreeUtil.getParentOfType(parent, RsStatementList::class.java) ?: return
        val name = DEFAULT_VAR_NAME
        val suggestedType = expression.type
        if (suggestedType is RsTupleType) {
            showErrorHint(project, editor, "The expression does not have a specific type.")
            return
        }
        if (suggestedType is RsErrorType) {
            showErrorHint(project, editor, "The expression does not have a valid type.")
            return
        }
        if (suggestedType is RsUnitType) {
            showErrorHint(project, editor, "Cannot introduce a variable of type 'unit'.")
            return
        }
        if (suggestedType !is RsPrimitiveType) {
            showErrorHint(project, editor, "Cannot introduce a variable of type '${suggestedType.representation}'.")
            return
        }
        WriteCommandAction.runWriteCommandAction(project) {
            val newVarDecl = RsElementGenerator.createLocalVariableDeclaration(
                project,
                suggestedType.representation,
                name,
                expression.text
            )

            val insertedDecl = parentBlock.addBefore(newVarDecl, parent)
            parentBlock.addBefore(RsElementGenerator.createNewLine(project), parent)

            val newVarRef = RsElementGenerator.createLocalVariableExpression(project, name)
            val replaced = expression.replace(newVarRef) as RsExpression

            // Unblock the document to ensure the editor reflects the changes
            PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.document)

            // Move the caret and attempt to inplace rename the variable
            val localExpr = PsiTreeUtil.findChildOfType(insertedDecl, RsLocalVariableExpression::class.java)
            if (localExpr != null) {
                moveCaretToName(editor, localExpr)
                VariableInplaceRenamer(localExpr, editor).performInplaceRefactoring(null)
            } else {
                editor.caretModel.moveToOffset(replaced.textOffset)
            }
        }
    }

    override fun invoke(
        project: Project,
        elements: Array<out PsiElement?>,
        dataContext: DataContext?
    ) {
    }

    private fun showErrorHint(project: Project, editor: Editor, message: String) {
        CommonRefactoringUtil.showErrorHint(
            project,
            editor,
            message,
            "Introduce Variable",
            null
        )
    }

    companion object {
        private const val DEFAULT_VAR_NAME = "var"
    }
}
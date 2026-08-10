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
import io.runescript.plugin.lang.psi.RsAssignmentStatement
import io.runescript.plugin.lang.psi.RsCommandExpression
import io.runescript.plugin.lang.psi.RsConstantExpression
import io.runescript.plugin.lang.psi.RsDynamicExpression
import io.runescript.plugin.lang.psi.RsElementGenerator
import io.runescript.plugin.lang.psi.RsExpression
import io.runescript.plugin.lang.psi.RsFile
import io.runescript.plugin.lang.psi.RsGosubExpression
import io.runescript.plugin.lang.psi.RsLocalVariableExpression
import io.runescript.plugin.lang.psi.RsPostfixExpression
import io.runescript.plugin.lang.psi.RsPrefixExpression
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.RsStatement
import io.runescript.plugin.lang.psi.RsStatementList
import io.runescript.plugin.lang.psi.RsWhileStatement
import io.runescript.plugin.lang.psi.isForArrayDeclaration
import io.runescript.plugin.lang.psi.isForVariableDeclaration
import io.runescript.plugin.lang.psi.typechecker.type.MetaType
import io.runescript.plugin.lang.psi.typechecker.type.TupleType
import io.runescript.plugin.lang.psi.typechecker.type.Type
import io.runescript.plugin.lang.psi.typechecker.typeCheckedType

class RsIntroduceVariableHandler : RsRefactoringActionBase() {
    override fun invoke(
        project: Project,
        editor: Editor,
        file: PsiFile,
        dataContext: DataContext,
    ) {
        if (file !is RsFile || !CommonRefactoringUtil.checkReadOnlyStatus(file)) return
        PsiDocumentManager.getInstance(project).commitDocument(editor.document)
        val expression = findTargetExpression(file, editor) ?: return
        val parent = PsiTreeUtil.getParentOfType(expression, RsStatement::class.java) ?: return
        val parentBlock = PsiTreeUtil.getParentOfType(parent, RsStatementList::class.java) ?: return
        val unsafeReason = expression.unsafeReason(parent, parentBlock)
        if (unsafeReason != null) {
            showErrorHint(project, editor, unsafeReason)
            return
        }
        val suggestedType = expression.typeCheckedType
        if (suggestedType is TupleType) {
            showErrorHint(project, editor, "The expression does not have a specific type.")
            return
        }
        if (suggestedType is MetaType.Error) {
            showErrorHint(project, editor, "The expression does not have a valid type.")
            return
        }
        if (suggestedType is MetaType.Unit) {
            showErrorHint(project, editor, "Cannot introduce a variable of type 'unit'.")
            return
        }
        val name = suggestName(expression, suggestedType)
        WriteCommandAction.runWriteCommandAction(project) {
            val newVarDecl =
                RsElementGenerator.createLocalVariableDeclaration(
                    project,
                    suggestedType.representation,
                    name,
                    expression.text,
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
        dataContext: DataContext?,
    ) {
    }

    private fun showErrorHint(
        project: Project,
        editor: Editor,
        message: String,
    ) {
        CommonRefactoringUtil.showErrorHint(
            project,
            editor,
            message,
            "Introduce Variable",
            null,
        )
    }

    private fun RsExpression.unsafeReason(
        statement: RsStatement,
        statementList: RsStatementList,
    ): String? {
        if (statement.parent !== statementList) {
            return "Cannot introduce a variable inside a brace-less control-flow body. Add braces first."
        }
        val assignment = PsiTreeUtil.getParentOfType(this, RsAssignmentStatement::class.java, false)
        if (assignment
                ?.expressionList
                ?.firstOrNull()
                ?.textRange
                ?.contains(textRange) == true
        ) {
            return "Cannot introduce a variable from an assignment target."
        }
        val local = this as? RsLocalVariableExpression
        if (local?.isForVariableDeclaration() == true || local?.isForArrayDeclaration() == true) {
            return "Cannot introduce a variable from a declaration name."
        }
        val mutatingExpression =
            PsiTreeUtil.getParentOfType(this, RsPostfixExpression::class.java, false)
                ?: PsiTreeUtil.getParentOfType(this, RsPrefixExpression::class.java, false)
        val isMutating = mutatingExpression?.text?.let { text -> "++" in text || "--" in text } == true
        if (isMutating) {
            return "Cannot introduce a variable from a mutating expression."
        }
        val whileStatement = PsiTreeUtil.getParentOfType(this, RsWhileStatement::class.java, false)
        if (whileStatement?.expression?.textRange?.contains(textRange) == true) {
            return "Cannot introduce a variable from a while condition because it must be evaluated repeatedly."
        }
        return null
    }

    private fun suggestName(
        expression: RsExpression,
        type: Type,
    ): String {
        val rawName =
            when (expression) {
                is RsLocalVariableExpression -> expression.name
                is RsConstantExpression -> expression.name
                is RsGosubExpression -> expression.name
                is RsCommandExpression -> expression.nameString
                is RsDynamicExpression -> expression.name
                else -> null
            }
        val fallback =
            when (type.representation) {
                "string" -> "text"
                "boolean" -> "flag"
                else -> "value"
            }
        val baseName = sanitizeName(rawName) ?: fallback
        val script = PsiTreeUtil.getParentOfType(expression, RsScript::class.java)
        val existingNames =
            script
                ?.let { PsiTreeUtil.findChildrenOfType(it, RsLocalVariableExpression::class.java) }
                .orEmpty()
                .filter { local -> local.isForVariableDeclaration() || local.isForArrayDeclaration() }
                .mapNotNullTo(hashSetOf()) { local -> local.name }
        if (baseName !in existingNames) return baseName
        var suffix = 2
        while ("$baseName$suffix" in existingNames) suffix++
        return "$baseName$suffix"
    }

    private fun sanitizeName(name: String?): String? {
        val candidate =
            name
                ?.substringAfterLast(':')
                ?.substringAfterLast('.')
                ?.removePrefix("get_")
                ?.removePrefix("is_")
                ?.replace(NON_IDENTIFIER_CHARACTERS, "_")
                ?.trim('_')
                ?.takeIf(String::isNotEmpty)
                ?: return null
        val validator = RsNamesValidator()
        return candidate.takeIf { validator.isIdentifier(it, null) && !validator.isKeyword(it, null) }
    }

    companion object {
        private val NON_IDENTIFIER_CHARACTERS = "[^A-Za-z0-9_]".toRegex()
    }
}

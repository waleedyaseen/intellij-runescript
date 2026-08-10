package io.runescript.plugin.ide.refactoring

import com.intellij.codeInsight.highlighting.ReadWriteAccessDetector.Access
import com.intellij.lang.Language
import com.intellij.lang.refactoring.InlineActionHandler
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.refactoring.util.CommonRefactoringUtil
import io.runescript.plugin.ide.codeInsight.isStableForDuplication
import io.runescript.plugin.ide.usages.RsReadWriteAccessDetector
import io.runescript.plugin.lang.RuneScript
import io.runescript.plugin.lang.psi.RsBinaryExpression
import io.runescript.plugin.lang.psi.RsElementGenerator
import io.runescript.plugin.lang.psi.RsExpression
import io.runescript.plugin.lang.psi.RsLocalVariableDeclarationStatement
import io.runescript.plugin.lang.psi.RsLocalVariableExpression
import io.runescript.plugin.lang.psi.RsScript

class RsInlineLocalHandler : InlineActionHandler() {
    override fun isEnabledForLanguage(language: Language): Boolean = language == RuneScript

    override fun canInlineElement(element: PsiElement): Boolean = element.inlineData() != null

    override fun inlineElement(
        project: Project,
        editor: Editor?,
        element: PsiElement,
    ) {
        val data = element.inlineData()
        if (data == null) {
            if (editor != null) {
                CommonRefactoringUtil.showErrorHint(
                    project,
                    editor,
                    "Only locals initialized with stable values can be inlined safely.",
                    "Inline Variable",
                    null,
                )
            }
            return
        }
        if (!CommonRefactoringUtil.checkReadOnlyStatus(data.declaration)) return

        WriteCommandAction.runWriteCommandAction(project, "Inline Variable", null, {
            val replacementText =
                if (data.initializer.needsParenthesesWhenInlined()) {
                    "(${data.initializer.text})"
                } else {
                    data.initializer.text
                }
            for (usage in data.usages.sortedByDescending(PsiElement::getTextOffset)) {
                usage.replace(RsElementGenerator.createExpression(project, replacementText))
            }
            val trailingWhitespace = data.declaration.nextSibling as? PsiWhiteSpace
            if (trailingWhitespace != null) {
                data.declaration.parent.deleteChildRange(data.declaration, trailingWhitespace)
            } else {
                data.declaration.delete()
            }
        }, data.declaration.containingFile)
    }

    private fun PsiElement.inlineData(): InlineData? {
        val local =
            when (this) {
                is RsLocalVariableExpression -> this
                else -> PsiTreeUtil.getParentOfType(this, RsLocalVariableExpression::class.java, false)
            } ?: return null
        val declarationLocal =
            if (local.parent is RsLocalVariableDeclarationStatement) {
                local
            } else {
                local.reference?.resolve() as? RsLocalVariableExpression
            } ?: return null
        val declaration = declarationLocal.parent as? RsLocalVariableDeclarationStatement ?: return null
        val initializer = declaration.initializer ?: return null
        if (!initializer.isStableForDuplication()) return null
        if (PsiTreeUtil.findChildOfType(declaration, PsiComment::class.java) != null) return null
        val script = PsiTreeUtil.getParentOfType(declaration, RsScript::class.java) ?: return null
        val usages =
            PsiTreeUtil
                .findChildrenOfType(script, RsLocalVariableExpression::class.java)
                .filter { candidate -> candidate !== declarationLocal && candidate.reference?.resolve() === declarationLocal }
        if (usages.isEmpty()) return null
        val accessDetector = RsReadWriteAccessDetector()
        if (usages.any { usage -> accessDetector.getExpressionAccess(usage) != Access.Read }) return null
        return InlineData(declaration, initializer, usages)
    }

    private fun RsExpression.needsParenthesesWhenInlined(): Boolean = this is RsBinaryExpression

    private data class InlineData(
        val declaration: RsLocalVariableDeclarationStatement,
        val initializer: RsExpression,
        val usages: List<RsLocalVariableExpression>,
    )
}

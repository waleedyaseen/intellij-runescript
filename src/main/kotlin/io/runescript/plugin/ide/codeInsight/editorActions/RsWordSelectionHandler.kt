package io.runescript.plugin.ide.codeInsight.editorActions

import com.intellij.codeInsight.editorActions.ExtendWordSelectionHandler
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import io.runescript.plugin.lang.RuneScript
import io.runescript.plugin.lang.RuneScriptHook
import io.runescript.plugin.lang.psi.RsArgumentList
import io.runescript.plugin.lang.psi.RsBlockStatement
import io.runescript.plugin.lang.psi.RsExpression
import io.runescript.plugin.lang.psi.RsNameLiteral
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.RsStatement
import io.runescript.plugin.lang.psi.RsStatementList

class RsWordSelectionHandler : ExtendWordSelectionHandler {
    override fun canSelect(element: PsiElement): Boolean = element.language == RuneScript || element.language == RuneScriptHook

    override fun select(
        element: PsiElement,
        editorText: CharSequence,
        cursorOffset: Int,
        editor: Editor,
    ): List<TextRange> =
        generateSequence(element, PsiElement::getParent)
            .filter(::isSemanticSelection)
            .map(PsiElement::getTextRange)
            .filter { it.contains(cursorOffset) }
            .distinct()
            .sortedBy(TextRange::getLength)
            .toList()

    private fun isSemanticSelection(element: PsiElement): Boolean =
        element is RsNameLiteral ||
            element is RsExpression ||
            element is RsArgumentList ||
            element is RsStatement ||
            element is RsStatementList ||
            element is RsBlockStatement ||
            element is RsScript
}

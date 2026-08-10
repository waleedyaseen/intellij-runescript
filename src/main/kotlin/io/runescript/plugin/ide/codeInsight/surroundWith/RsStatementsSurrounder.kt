package io.runescript.plugin.ide.codeInsight.surroundWith

import com.intellij.lang.surroundWith.Surrounder
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleManager
import io.runescript.plugin.lang.psi.RsStatement
import io.runescript.plugin.lang.psi.RsStatementList

abstract class RsStatementsSurrounder<T : RsStatement> : Surrounder {
    final override fun isApplicable(elements: Array<out PsiElement>): Boolean =
        elements.isNotEmpty() &&
            elements.all { it is RsStatement } &&
            elements.map { it.parent }.distinct().singleOrNull() is RsStatementList

    final override fun surroundElements(
        project: Project,
        editor: Editor,
        elements: Array<out PsiElement>,
    ): TextRange? {
        if (!isApplicable(elements)) return null

        val first = elements.first() as RsStatement
        val last = elements.last() as RsStatement
        val statementList = first.parent as RsStatementList
        val source = first.containingFile.text.substring(first.textRange.startOffset, last.textRange.endOffset)
        val template = createTemplate(project, source)

        @Suppress("UNCHECKED_CAST")
        val inserted = statementList.addBefore(template, first) as T
        statementList.deleteChildRange(first, last)

        @Suppress("UNCHECKED_CAST")
        val formatted = CodeStyleManager.getInstance(project).reformat(inserted) as T
        return getSelectionRange(formatted)
    }

    protected abstract fun createTemplate(
        project: Project,
        source: String,
    ): T

    protected abstract fun getSelectionRange(statement: T): TextRange?
}

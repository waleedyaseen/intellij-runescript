package io.runescript.plugin.ide.codeInsight.surroundWith

import com.intellij.lang.surroundWith.Surrounder
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleManager
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.lang.psi.RsElementGenerator
import io.runescript.plugin.lang.psi.RsStatement
import io.runescript.plugin.lang.psi.RsStatementList
import io.runescript.plugin.lang.psi.RsWhileStatement

class RsWithWhileSurrounder : Surrounder {
    override fun getTemplateDescription(): String = RsBundle.message("surround.with.while.template")

    override fun isApplicable(elements: Array<out PsiElement>): Boolean =
        elements.isNotEmpty() &&
            elements.all { it is RsStatement } &&
            elements.map { it.parent }.distinct().singleOrNull() is RsStatementList

    override fun surroundElements(
        project: Project,
        editor: Editor,
        elements: Array<out PsiElement>,
    ): TextRange? {
        if (!isApplicable(elements)) return null

        val first = elements.first() as RsStatement
        val last = elements.last() as RsStatement
        val statementList = first.parent as RsStatementList
        val source = first.containingFile.text.substring(first.textRange.startOffset, last.textRange.endOffset)
        val template = RsElementGenerator.createStatement(project, "while (true) {\n$source\n}")
        val inserted = statementList.addBefore(template, first) as RsWhileStatement
        statementList.deleteChildRange(first, last)
        val formatted = CodeStyleManager.getInstance(project).reformat(inserted) as RsWhileStatement
        return formatted.expression?.textRange
    }
}

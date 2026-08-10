package io.runescript.plugin.ide.codeInsight.editorActions

import com.intellij.codeInsight.editorActions.moveUpDown.LineRange
import com.intellij.codeInsight.editorActions.moveUpDown.StatementUpDownMover
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.lang.psi.RsFile
import io.runescript.plugin.lang.psi.RsStatement
import io.runescript.plugin.lang.psi.RsStatementList
import io.runescript.plugin.lang.psi.RsSwitchCase
import io.runescript.plugin.lang.psi.RsSwitchCaseDefaultExpression
import io.runescript.plugin.lang.psi.RsSwitchStatement

class RsStatementMover : StatementUpDownMover() {
    override fun checkAvailable(
        editor: Editor,
        file: PsiFile,
        info: MoveInfo,
        down: Boolean,
    ): Boolean {
        if (file !is RsFile) return false
        if (editor.selectionModel.hasSelection()) return info.prohibitMove()
        if (file.textLength == 0) return false
        val leaf = file.findElementAt(editor.caretModel.offset.coerceAtMost(file.textLength - 1)) ?: return false
        val switchCase = leaf.parentOfType<RsSwitchCase>()
        val target =
            if (switchCase != null && editor.caretModel.offset <= switchCase.colon.textRange.endOffset) {
                findSwitchCaseMove(switchCase, down) ?: return info.prohibitMove()
            } else {
                val statement = leaf.parentOfType<RsStatement>() ?: return false
                findStatementMove(statement, down) ?: return info.prohibitMove()
            }

        val sourceRange = target.first.lineRangeWithLeadingComments(editor)
        val destinationRange = target.second.lineRangeWithLeadingComments(editor)
        if (sourceRange.startLine < destinationRange.endLine && destinationRange.startLine < sourceRange.endLine) return false
        info.toMove = sourceRange
        info.toMove2 = destinationRange
        info.indentSource = false
        info.indentTarget = false
        return true
    }

    private fun findStatementMove(
        statement: RsStatement,
        down: Boolean,
    ): Pair<PsiElement, PsiElement>? {
        val statementList = statement.parent as? RsStatementList ?: return null
        val statements = statementList.statementList
        return statement.withAdjacent(statements, down)
    }

    private fun findSwitchCaseMove(
        switchCase: RsSwitchCase,
        down: Boolean,
    ): Pair<PsiElement, PsiElement>? {
        if (switchCase.hasDefaultLabel()) return null
        val switch = switchCase.parent as? RsSwitchStatement ?: return null
        val cases = switch.switchCaseList
        val defaultIndexes = cases.indices.filter { cases[it].hasDefaultLabel() }
        if (defaultIndexes.size > 1 || defaultIndexes.singleOrNull()?.let { it != cases.lastIndex } == true) return null
        val move = switchCase.withAdjacent(cases, down) ?: return null
        if ((move.second as RsSwitchCase).hasDefaultLabel()) return null
        return move
    }

    private fun <T : PsiElement> T.withAdjacent(
        elements: List<T>,
        down: Boolean,
    ): Pair<PsiElement, PsiElement>? {
        val index = elements.indexOf(this)
        val adjacent = elements.getOrNull(index + if (down) 1 else -1) ?: return null
        return this to adjacent
    }

    private fun RsSwitchCase.hasDefaultLabel(): Boolean = expressionList.any { it is RsSwitchCaseDefaultExpression }

    private fun PsiElement.lineRangeWithLeadingComments(editor: Editor): LineRange {
        val elementRange = LineRange(this)
        var startLine = elementRange.startLine
        var sibling = prevSibling
        while (sibling != null) {
            when (sibling) {
                is PsiWhiteSpace -> {
                    if (sibling.text.count { it == '\n' } > 1) break
                }

                is PsiComment -> {
                    val commentLine = editor.document.getLineNumber(sibling.textOffset)
                    val previous = sibling.previousNonWhitespaceSibling()
                    if (previous != null && editor.document.getLineNumber(previous.textRange.endOffset - 1) == commentLine) break
                    startLine = commentLine
                }

                else -> {
                    break
                }
            }
            sibling = sibling.prevSibling
        }
        return LineRange(startLine, elementRange.endLine)
    }

    private fun PsiElement.previousNonWhitespaceSibling(): PsiElement? {
        var sibling = prevSibling
        while (sibling is PsiWhiteSpace) sibling = sibling.prevSibling
        return sibling
    }

    private inline fun <reified T : PsiElement> PsiElement.parentOfType(): T? = PsiTreeUtil.getParentOfType(this, T::class.java, false)
}

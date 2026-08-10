package io.runescript.plugin.ide.codeInsight.surroundWith

import com.intellij.lang.surroundWith.SurroundDescriptor
import com.intellij.lang.surroundWith.Surrounder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.lang.psi.RsFile
import io.runescript.plugin.lang.psi.RsStatement
import io.runescript.plugin.lang.psi.RsStatementList

class RsStatementsSurroundDescriptor : SurroundDescriptor {
    override fun getElementsToSurround(
        file: PsiFile,
        startOffset: Int,
        endOffset: Int,
    ): Array<PsiElement> {
        if (file !is RsFile || startOffset >= endOffset) return PsiElement.EMPTY_ARRAY

        val first = findStatement(file, startOffset, endOffset, forward = true) ?: return PsiElement.EMPTY_ARRAY
        val last = findStatement(file, startOffset, endOffset, forward = false) ?: return PsiElement.EMPTY_ARRAY
        val statementList = first.parent as? RsStatementList ?: return PsiElement.EMPTY_ARRAY
        if (last.parent !== statementList) return PsiElement.EMPTY_ARRAY

        val statements = statementList.statementList
        val firstIndex = statements.indexOf(first)
        val lastIndex = statements.indexOf(last)
        if (firstIndex < 0 || lastIndex < firstIndex) return PsiElement.EMPTY_ARRAY
        return statements.subList(firstIndex, lastIndex + 1).toTypedArray()
    }

    override fun getSurrounders(): Array<Surrounder> = arrayOf(RsWithIfSurrounder())

    override fun isExclusive(): Boolean = false

    private fun findStatement(
        file: PsiFile,
        startOffset: Int,
        endOffset: Int,
        forward: Boolean,
    ): RsStatement? {
        var leaf = file.findElementAt(if (forward) startOffset else endOffset - 1)
        while (leaf != null && leaf.textRange.intersects(startOffset, endOffset)) {
            generateSequence(PsiTreeUtil.getParentOfType(leaf, RsStatement::class.java, false)) {
                PsiTreeUtil.getParentOfType(it, RsStatement::class.java, true)
            }.firstOrNull { it.parent is RsStatementList }?.let { return it }
            leaf = if (forward) PsiTreeUtil.nextLeaf(leaf) else PsiTreeUtil.prevLeaf(leaf)
        }
        return null
    }
}

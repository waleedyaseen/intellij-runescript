package io.runescript.plugin.ide.completion.insertHandler

import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.psi.util.endOffset
import com.intellij.psi.util.findParentOfType
import com.intellij.psi.util.startOffset
import io.runescript.plugin.lang.psi.RsLocalVariableExpression

object RsVariableInsertHandler : InsertHandler<LookupElement> {
    override fun handleInsert(context: InsertionContext, item: LookupElement) {
        val element = item.psiElement as? RsLocalVariableExpression ?: return
        val name = element.name ?: return
        val position = context.file.findElementAt(context.startOffset) ?: return
        val parentVar = position.findParentOfType<RsLocalVariableExpression>()
        val startOffset = parentVar?.startOffset ?: context.startOffset
        val endOffset = parentVar?.endOffset ?: context.tailOffset
        context.commitDocument()
        val replacementText = "$$name"
        context.document.replaceString(startOffset, endOffset, replacementText)
        context.editor.caretModel.moveToOffset(startOffset + replacementText.length)
    }
}
package io.runescript.plugin.ide.completion.insertHandler

import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement

class RsScriptInsertHandler(
    private val prefix: String? = null
) : InsertHandler<LookupElement> {
    override fun handleInsert(
        context: InsertionContext,
        item: LookupElement
    ) {
        val document = context.document
        val editor = context.editor
        var endOffset = context.tailOffset


        if (prefix != null) {
            val startOffset = context.startOffset
            document.insertString(startOffset, prefix)
            endOffset += prefix.length
        }
        document.insertString(endOffset, "()")
        editor.caretModel.moveToOffset(endOffset + 1)

        context.commitDocument()

        val position = context.file.findElementAt(context.startOffset) ?: return
        val popupController = AutoPopupController.getInstance(context.project)
        popupController.autoPopupParameterInfo(editor, position)
    }
}
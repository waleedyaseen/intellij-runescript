package io.runescript.plugin.ide.completion.insertHandler

import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement
import io.runescript.plugin.ide.completion.insertHandler.RsInsertHandlerUtil.hasPrefixAt

class RsScriptInsertHandler(
    private val prefix: String? = null,
) : InsertHandler<LookupElement> {
    override fun handleInsert(
        context: InsertionContext,
        item: LookupElement,
    ) {
        val document = context.document
        val editor = context.editor
        val startOffset = RsInsertHandlerUtil.findIdentifierStart(context)
        val replacementText = createReplacementText(document.charsSequence, startOffset, item.lookupString)
        document.replaceString(startOffset, context.tailOffset, replacementText)
        val endOffset = startOffset + replacementText.length
        val existingArgumentListOffset = findExistingArgumentListOffset(document.charsSequence, endOffset)
        if (existingArgumentListOffset != null) {
            editor.caretModel.moveToOffset(existingArgumentListOffset + 1)
        } else {
            document.insertString(endOffset, "()")
            editor.caretModel.moveToOffset(endOffset + 1)
        }

        context.commitDocument()

        val position = context.file.findElementAt(context.startOffset) ?: return
        val popupController = AutoPopupController.getInstance(context.project)
        popupController.autoPopupParameterInfo(editor, position)
    }

    private fun createReplacementText(
        text: CharSequence,
        startOffset: Int,
        lookupString: String,
    ): String {
        if (prefix == null || text.hasPrefixAt(startOffset, prefix)) {
            return lookupString
        }
        return "$prefix$lookupString"
    }

    private fun findExistingArgumentListOffset(
        text: CharSequence,
        offset: Int,
    ): Int? {
        var index = offset
        while (index < text.length && text[index].isWhitespace()) {
            index++
        }
        return if (index < text.length && text[index] == '(') index else null
    }
}

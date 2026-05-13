package io.runescript.plugin.ide.completion.insertHandler

import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement

class RsKeywordSnippetInsertHandler(
    private val snippet: String,
) : InsertHandler<LookupElement> {
    override fun handleInsert(
        context: InsertionContext,
        item: LookupElement,
    ) {
        val startOffset = RsInsertHandlerUtil.findIdentifierStart(context)
        val markerIndex = snippet.indexOf(CARET_MARKER)
        val replacementText = snippet.replace(CARET_MARKER.toString(), "")
        context.document.replaceString(startOffset, context.tailOffset, replacementText)
        if (markerIndex >= 0) {
            context.editor.caretModel.moveToOffset(startOffset + markerIndex)
        }
        context.commitDocument()
    }

    companion object {
        const val CARET_MARKER: Char = '\u0001'
    }
}

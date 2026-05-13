package io.runescript.plugin.ide.completion.insertHandler

import com.intellij.codeInsight.completion.InsertionContext

internal object RsInsertHandlerUtil {
    fun findIdentifierStart(
        context: InsertionContext,
        marker: Char? = null,
    ): Int {
        val text = context.document.charsSequence
        var startOffset = context.startOffset
        while (startOffset > 0 && isIdentifierPart(text[startOffset - 1])) {
            startOffset--
        }
        if (marker != null && startOffset > 0 && text[startOffset - 1] == marker) {
            startOffset--
        }
        return startOffset
    }

    fun replaceAndMoveCaret(
        context: InsertionContext,
        startOffset: Int,
        replacementText: String,
    ) {
        context.document.replaceString(startOffset, context.tailOffset, replacementText)
        context.editor.caretModel.moveToOffset(startOffset + replacementText.length)
        context.commitDocument()
    }

    fun findOperatorStart(context: InsertionContext): Int {
        val text = context.document.charsSequence
        var startOffset = context.startOffset
        while (startOffset > 0 && (isOperatorPart(text[startOffset - 1]) || text[startOffset - 1].isWhitespace())) {
            startOffset--
        }
        return startOffset
    }

    fun CharSequence.hasPrefixAt(
        offset: Int,
        prefix: String,
    ): Boolean {
        val startOffset = offset - prefix.length
        return startOffset >= 0 && subSequence(startOffset, offset).toString() == prefix
    }

    private fun isIdentifierPart(char: Char): Boolean = char.isLetterOrDigit() || char == '_' || char == '.' || char == ':'

    private fun isOperatorPart(char: Char): Boolean = char == '=' || char == '!' || char == '>' || char == '<'
}

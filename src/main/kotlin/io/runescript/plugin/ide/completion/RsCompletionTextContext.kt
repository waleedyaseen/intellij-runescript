package io.runescript.plugin.ide.completion

import com.intellij.openapi.editor.Document

internal data class RsCompletionCallInfo(
    val name: String,
    val isProc: Boolean,
    val argumentIndex: Int,
)

internal fun currentCall(
    text: String,
    offset: Int,
): RsCompletionCallInfo? {
    val prefix = currentStatementPrefix(text, offset)
    val openParen = prefix.lastIndexOf('(')
    if (openParen < 0) {
        return null
    }
    val closeParen = prefix.indexOf(')', openParen)
    if (closeParen >= 0) {
        return null
    }
    var nameEnd = openParen
    while (nameEnd > 0 && prefix[nameEnd - 1].isWhitespace()) {
        nameEnd--
    }
    var nameStart = nameEnd
    while (nameStart > 0 && isIdentifierPart(prefix[nameStart - 1])) {
        nameStart--
    }
    if (nameStart == nameEnd) {
        return null
    }
    val isProc = nameStart > 0 && prefix[nameStart - 1] == '~'
    val argumentIndex = prefix.substring(openParen + 1).count { it == ',' }
    return RsCompletionCallInfo(prefix.substring(nameStart, nameEnd), isProc, argumentIndex)
}

internal fun currentStatementPrefix(
    text: String,
    offset: Int,
): String {
    var index = (offset - 1).coerceAtMost(text.lastIndex)
    while (index >= 0) {
        when (text[index]) {
            '{', '}', ';' -> return text.substring(index + 1, offset)
        }
        index--
    }
    return text.substring(0, offset.coerceIn(0, text.length))
}

internal fun currentLinePrefix(
    document: Document,
    offset: Int,
): String {
    val lineStart = document.getLineStartOffset(document.getLineNumber(offset))
    return document.text.substring(lineStart, offset)
}

internal fun insideOpenSwitchBody(
    text: String,
    offset: Int,
): Boolean {
    val beforeCaret = text.substring(0, offset.coerceIn(0, text.length))
    val switchIndex = beforeCaret.lastIndexOf("switch_")
    if (switchIndex < 0) {
        return false
    }
    val openBrace = beforeCaret.indexOf('{', switchIndex)
    if (openBrace < 0) {
        return false
    }
    val closeBrace = beforeCaret.indexOf('}', openBrace)
    return closeBrace < 0
}

internal fun currentWordPrefix(
    text: String,
    offset: Int,
): String {
    var index = (offset - 1).coerceAtMost(text.lastIndex)
    while (index >= 0 && isIdentifierPart(text[index])) {
        index--
    }
    return text.substring(index + 1, offset)
}

private fun isIdentifierPart(char: Char): Boolean = char.isLetterOrDigit() || char == '_' || char == '.' || char == ':'

internal fun previousNonWhitespace(
    text: String,
    offset: Int,
): Char? {
    var index = (offset - 1).coerceAtMost(text.lastIndex)
    while (index >= 0) {
        val char = text[index]
        if (!char.isWhitespace()) {
            return char
        }
        index--
    }
    return null
}

internal fun firstLineBreakAfter(
    text: String,
    offset: Int,
): Int? {
    val newline = text.indexOf('\n', offset).takeIf { it >= 0 }
    val carriageReturn = text.indexOf('\r', offset).takeIf { it >= 0 }
    return listOfNotNull(newline, carriageReturn).minOrNull()
}

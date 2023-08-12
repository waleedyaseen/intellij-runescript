package io.runescript.plugin.ide.doc

import com.intellij.lang.documentation.DocumentationSettings
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.richcopy.HtmlSyntaxInfoUtil
import io.runescript.plugin.ide.highlight.RsSyntaxHighlighterColors

class RsDocumentationGenerator {

    private val builder = StringBuilder()

    fun appendLParen() {
        appendHighlighted("(", RsSyntaxHighlighterColors.PARENTHESIS)
    }

    fun appendRParen() {
        appendHighlighted(")", RsSyntaxHighlighterColors.PARENTHESIS)
    }

    fun appendComma(text: String = ",") {
        appendHighlighted(text, RsSyntaxHighlighterColors.COMMA)
    }

    fun appendKeyword(keyword: String) {
        appendHighlighted(keyword, RsSyntaxHighlighterColors.KEYWORD)
    }

    fun appendHighlighted(keyword: String, attributes: TextAttributesKey) {
        @Suppress("UnstableApiUsage")
        HtmlSyntaxInfoUtil.appendStyledSpan(
            builder,
            attributes,
            keyword,
            DocumentationSettings.getHighlightingSaturation(false)
        )
    }

    fun appendColon(text: String = ":") {
        appendHighlighted(text, RsSyntaxHighlighterColors.COLON)
    }

    fun build(): String {
        return builder.toString()
    }
}
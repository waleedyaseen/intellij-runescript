package io.runescript.plugin.ide.doc

import com.intellij.lang.documentation.DocumentationSettings
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.editor.richcopy.HtmlSyntaxInfoUtil
import com.intellij.openapi.project.Project
import io.runescript.plugin.ide.highlight.RsSyntaxHighlighterColors
import io.runescript.plugin.lang.RuneScript

class RsDocumentationGenerator(private val project: Project) {

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

    fun appendHighlighted(value: String, attributes: TextAttributes) {
        HtmlSyntaxInfoUtil.appendStyledSpan(
                builder,
                attributes,
                value,
                DocumentationSettings.getHighlightingSaturation(false)
        )
    }

    fun appendHighlighted(keyword: String, attributes: TextAttributesKey) {
        HtmlSyntaxInfoUtil.appendStyledSpan(
                builder,
                attributes,
                keyword,
                DocumentationSettings.getHighlightingSaturation(false)
        )
    }

    fun appendCode(code: String) {
        HtmlSyntaxInfoUtil.appendHighlightedByLexerAndEncodedAsHtmlCodeSnippet(
                builder,
                project,
                RuneScript,
                code,
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
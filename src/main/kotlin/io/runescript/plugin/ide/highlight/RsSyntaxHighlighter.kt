package io.runescript.plugin.ide.highlight

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import io.runescript.plugin.lang.lexer.RsLexerAdapter
import io.runescript.plugin.lang.lexer.RsLexerInfo
import io.runescript.plugin.lang.psi.RsElementTypes.*
import io.runescript.plugin.lang.psi.RsTokenTypes.MULTI_LINE_COMMENT
import io.runescript.plugin.lang.psi.RsTokenTypes.SINGLE_LINE_COMMENT
import io.runescript.plugin.lang.psi.RsTokenTypesSets.BRACES
import io.runescript.plugin.lang.psi.RsTokenTypesSets.BRACKETS
import io.runescript.plugin.lang.psi.RsTokenTypesSets.KEYWORDS
import io.runescript.plugin.lang.psi.RsTokenTypesSets.OPERATORS
import io.runescript.plugin.lang.psi.RsTokenTypesSets.PARENS

class RsSyntaxHighlighter(private val lexerInfo: RsLexerInfo) : SyntaxHighlighterBase() {

    override fun getHighlightingLexer(): Lexer {
        return RsLexerAdapter(lexerInfo)
    }

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> {
        return pack(attributes[tokenType])
    }

    companion object {
        private val attributes = mutableMapOf<IElementType, TextAttributesKey>()

        init {
            attributes[IDENTIFIER] = RsSyntaxHighlighterColors.IDENTIFIER
            attributes[INTEGER] = RsSyntaxHighlighterColors.NUMBER
            fillMap(attributes, KEYWORDS, RsSyntaxHighlighterColors.KEYWORD)
            attributes[TYPE_LITERAL] = RsSyntaxHighlighterColors.TYPE_LITERAL
            attributes[ARRAY_TYPE_LITERAL] = RsSyntaxHighlighterColors.ARRAY_TYPE_LITERAL
            fillMap(attributes, TokenSet.create(STRING_START, STRING_PART, STRING_END), RsSyntaxHighlighterColors.STRING)
            attributes[STRING_TAG] = RsSyntaxHighlighterColors.STRING_TAG
            attributes[MULTI_LINE_COMMENT] = RsSyntaxHighlighterColors.BLOCK_COMMENT
            attributes[SINGLE_LINE_COMMENT] = RsSyntaxHighlighterColors.LINE_COMMENT
            fillMap(attributes, OPERATORS, RsSyntaxHighlighterColors.OPERATION_SIGN)
            fillMap(attributes, BRACES, RsSyntaxHighlighterColors.BRACES)
            attributes[SEMICOLON] = RsSyntaxHighlighterColors.SEMICOLON
            attributes[COLON] = RsSyntaxHighlighterColors.COLON
            attributes[COMMA] = RsSyntaxHighlighterColors.COMMA
            fillMap(attributes, PARENS, RsSyntaxHighlighterColors.PARENTHESIS)
            fillMap(attributes, BRACKETS, RsSyntaxHighlighterColors.BRACKETS)
        }
    }
}
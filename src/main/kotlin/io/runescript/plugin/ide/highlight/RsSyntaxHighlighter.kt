package io.runescript.plugin.ide.highlight

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import io.runescript.plugin.lang.lexer.RuneScriptLexerAdapter
import io.runescript.plugin.lang.lexer.RuneScriptLexerInfo
import io.runescript.plugin.lang.psi.RuneScriptTokenTypes
import io.runescript.plugin.lang.psi.RuneScriptTokenTypes.MULTI_LINE_COMMENT
import io.runescript.plugin.lang.psi.RuneScriptTokenTypes.SINGLE_LINE_COMMENT
import io.runescript.plugin.lang.psi.RuneScriptTokenTypesSets.BRACES
import io.runescript.plugin.lang.psi.RuneScriptTokenTypesSets.BRACKETS
import io.runescript.plugin.lang.psi.RuneScriptTokenTypesSets.KEYWORDS
import io.runescript.plugin.lang.psi.RuneScriptTokenTypesSets.OPERATORS
import io.runescript.plugin.lang.psi.RuneScriptTokenTypesSets.PARENS
import io.runescript.plugin.lang.psi.RuneScriptTypes.*

class RsSyntaxHighlighter(private val lexerInfo: RuneScriptLexerInfo) : SyntaxHighlighterBase() {

    override fun getHighlightingLexer(): Lexer {
        return RuneScriptLexerAdapter(lexerInfo)
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
            attributes[TYPE_NAME] = RsSyntaxHighlighterColors.TYPE_NAME
            attributes[ARRAY_TYPE_NAME] = RsSyntaxHighlighterColors.ARRAY_TYPE_NAME
            fillMap(attributes, TokenSet.create(STRING_START, STRING_PART, STRING_END), RsSyntaxHighlighterColors.STRING)
            attributes[STRING_TAG] = RsSyntaxHighlighterColors.STRING_TAG
            attributes[MULTI_LINE_COMMENT] = RsSyntaxHighlighterColors.BLOCK_COMMENT
            attributes[SINGLE_LINE_COMMENT] = RsSyntaxHighlighterColors.LINE_COMMENT
            fillMap(attributes, OPERATORS, RsSyntaxHighlighterColors.OPERATION_SIGN)
            fillMap(attributes, BRACES, RsSyntaxHighlighterColors.BRACES)
            attributes[SEMICOLON] = RsSyntaxHighlighterColors.SEMICOLON
            attributes[COMMA] = RsSyntaxHighlighterColors.COMMA
            fillMap(attributes, PARENS, RsSyntaxHighlighterColors.PARENTHESIS)
            fillMap(attributes, BRACKETS, RsSyntaxHighlighterColors.BRACKETS)
        }
    }
}
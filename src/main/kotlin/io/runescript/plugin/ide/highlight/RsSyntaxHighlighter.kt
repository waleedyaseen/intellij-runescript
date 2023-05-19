package io.runescript.plugin.ide.highlight

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.openapi.project.Project
import com.intellij.psi.tree.IElementType
import io.runescript.plugin.lang.lexer.RuneScriptLexerAdapter
import io.runescript.plugin.lang.psi.RuneScriptTokenTypesSets.BRACES
import io.runescript.plugin.lang.psi.RuneScriptTokenTypesSets.BRACKETS
import io.runescript.plugin.lang.psi.RuneScriptTokenTypesSets.KEYWORDS
import io.runescript.plugin.lang.psi.RuneScriptTokenTypesSets.OPERATORS
import io.runescript.plugin.lang.psi.RuneScriptTokenTypesSets.PARENS
import io.runescript.plugin.lang.psi.RuneScriptTypes.*

class RsSyntaxHighlighter(private val project: Project) : SyntaxHighlighterBase() {

    override fun getHighlightingLexer(): Lexer {
        return RuneScriptLexerAdapter(project)
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
            attributes[STRING_PART] = RsSyntaxHighlighterColors.STRING
            attributes[STRING_TAG] = RsSyntaxHighlighterColors.STRING // TODO(walied): different color?
            // TODO(walied): block comment
            // TODO(walied): line comment
            fillMap(attributes, OPERATORS, RsSyntaxHighlighterColors.OPERATION_SIGN)
            fillMap(attributes, BRACES, RsSyntaxHighlighterColors.BRACES)
            attributes[SEMICOLON] = RsSyntaxHighlighterColors.SEMICOLON
            attributes[COMMA] = RsSyntaxHighlighterColors.COMMA
            fillMap(attributes, PARENS, RsSyntaxHighlighterColors.PARENTHESIS)
            fillMap(attributes, BRACKETS, RsSyntaxHighlighterColors.BRACKETS)
        }
    }
}
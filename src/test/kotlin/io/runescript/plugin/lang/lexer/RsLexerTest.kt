package io.runescript.plugin.lang.lexer

import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.runescript.plugin.ide.neptune.DEFAULT_RESOLVED_DATA
import io.runescript.plugin.lang.psi.RsElementTypes
import io.runescript.plugin.lang.psi.RsTokenTypes

class RsLexerTest : BasePlatformTestCase() {
    fun testCommentsAndBadCharactersLex() {
        assertTokenTypes(
            """
            // line
            /** doc */
            /* block */
            /**/
            /* eof
            """,
            RsTokenTypes.LINE_COMMENT,
            RsTokenTypes.DOC_COMMENT,
            RsTokenTypes.BLOCK_COMMENT,
            RsTokenTypes.BLOCK_COMMENT,
            RsTokenTypes.BLOCK_COMMENT,
        )
    }

    fun testDocCommentAtEofLex() {
        assertTokenTypes("/** eof", RsTokenTypes.DOC_COMMENT)
    }

    fun testBadCharactersLex() {
        assertTokenTypes("`", TokenType.BAD_CHARACTER)
    }

    fun testFixedKeywordsLex() {
        assertTokenTypes(
            "if else while true false null case default calc return",
            RsElementTypes.IF,
            RsElementTypes.ELSE,
            RsElementTypes.WHILE,
            RsElementTypes.TRUE,
            RsElementTypes.FALSE,
            RsElementTypes.NULL,
            RsElementTypes.CASE,
            RsElementTypes.DEFAULT,
            RsElementTypes.CALC,
            RsElementTypes.RETURN,
        )
    }

    fun testStringTagsEscapesAndInterpolationLex() {
        assertTokenTypes(
            """"a\n<col=ff00ff><br></col><img=1><shad=<calc(1 + 2)>z"""",
            RsElementTypes.STRING_START,
            RsElementTypes.STRING_PART,
            RsElementTypes.STRING_TAG,
            RsElementTypes.STRING_TAG,
            RsElementTypes.STRING_TAG,
            RsElementTypes.STRING_TAG,
            RsElementTypes.STRING_PART,
            RsElementTypes.STRING_INTERPOLATION_START,
            RsElementTypes.CALC,
            RsElementTypes.LPAREN,
            RsElementTypes.INTEGER,
            RsElementTypes.PLUS,
            RsElementTypes.INTEGER,
            RsElementTypes.RPAREN,
            RsElementTypes.STRING_INTERPOLATION_END,
            RsElementTypes.STRING_PART,
            RsElementTypes.STRING_END,
        )
    }

    fun testBadCharacterInsideStringLex() {
        assertTokenTypes(
            """"bad\q"""",
            RsElementTypes.STRING_START,
            RsElementTypes.STRING_PART,
            TokenType.BAD_CHARACTER,
            RsElementTypes.STRING_PART,
            RsElementTypes.STRING_END,
        )
    }

    fun testNumericAndOperatorTokensLex() {
        assertTokenTypes(
            """
            -1 +2 0x2a -0x2a 123L -0x7bL ++ --
            ! > >= < <= = & | + - * / % ~ ^ $ , : ; ( ) { } [ ]
            """,
            RsElementTypes.INTEGER,
            RsElementTypes.INTEGER,
            RsElementTypes.INTEGER,
            RsElementTypes.INTEGER,
            RsElementTypes.LONG,
            RsElementTypes.LONG,
            RsElementTypes.PLUS,
            RsElementTypes.PLUS,
            RsElementTypes.MINUS,
            RsElementTypes.MINUS,
            RsElementTypes.EXCEL,
            RsElementTypes.GT,
            RsElementTypes.GTE,
            RsElementTypes.LT,
            RsElementTypes.LTE,
            RsElementTypes.EQUAL,
            RsElementTypes.AMPERSAND,
            RsElementTypes.BAR,
            RsElementTypes.PLUS,
            RsElementTypes.MINUS,
            RsElementTypes.STAR,
            RsElementTypes.SLASH,
            RsElementTypes.PERCENT,
            RsElementTypes.TILDE,
            RsElementTypes.CARET,
            RsElementTypes.DOLLAR,
            RsElementTypes.COMMA,
            RsElementTypes.COLON,
            RsElementTypes.SEMICOLON,
            RsElementTypes.LPAREN,
            RsElementTypes.RPAREN,
            RsElementTypes.LBRACE,
            RsElementTypes.RBRACE,
            RsElementTypes.LBRACKET,
            RsElementTypes.RBRACKET,
        )
    }

    fun testDynamicTypeKeywordsLexFromTypeManager() {
        assertTokenTypes(
            "int def_int switch_int proc",
            RsElementTypes.TYPE_LITERAL,
            RsElementTypes.DEFINE_TYPE,
            RsElementTypes.SWITCH,
            RsElementTypes.IDENTIFIER,
        )
    }

    private fun assertTokenTypes(
        text: String,
        vararg expected: IElementType,
    ) {
        assertEquals(expected.toList(), tokenTypes(text.trimIndent()))
    }

    private fun tokenTypes(text: String): List<IElementType> {
        val lexer = RsLexerAdapter(RsLexerInfo(DEFAULT_RESOLVED_DATA.types))
        lexer.start(text)
        val tokens = mutableListOf<IElementType>()
        while (true) {
            val tokenType = lexer.tokenType ?: break
            if (tokenType != TokenType.WHITE_SPACE) {
                tokens.add(tokenType)
            }
            lexer.advance()
        }
        return tokens
    }
}

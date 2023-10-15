package io.runescript.plugin.lang.psi

import com.intellij.psi.tree.TokenSet
import io.runescript.plugin.lang.psi.RsElementTypes.*
import io.runescript.plugin.lang.psi.RsTokenTypes.DOC_COMMENT
import io.runescript.plugin.lang.psi.RsTokenTypes.BLOCK_COMMENT
import io.runescript.plugin.lang.psi.RsTokenTypes.LINE_COMMENT

object RsTokenTypesSets {

    val KEYWORDS = TokenSet.create(
        IF,
        ELSE,
        WHILE,
        SWITCH,
        CASE,
        DEFAULT,
        RETURN,
        DEFINE_TYPE,
        CALC,
        RETURN,
        TRUE,
        FALSE,
        NULL
    )
    val OPERATORS = TokenSet.create(
        PLUS,
        MINUS,
        STAR,
        SLASH,
        PERCENT,
        BAR,
        AMPERSAND,
        GT,
        GTE,
        LT,
        LTE,
        EXCEL,
        EQUAL,
    )
    val BRACES = TokenSet.create(
        LBRACE,
        RBRACE,
    )
    val BRACKETS = TokenSet.create(
        LBRACKET,
        RBRACKET,
    )
    val PARENS = TokenSet.create(
        LPAREN,
        RPAREN,
    )
    val COMMENTS = TokenSet.create(
        LINE_COMMENT,
        BLOCK_COMMENT,
        DOC_COMMENT
    )

    val STRING_ELEMENTS = TokenSet.create(STRING_START, STRING_PART, STRING_TAG, STRING_END)
}
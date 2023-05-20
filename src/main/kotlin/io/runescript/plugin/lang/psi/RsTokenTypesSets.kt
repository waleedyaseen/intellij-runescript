package io.runescript.plugin.lang.psi

import com.intellij.psi.tree.TokenSet
import io.runescript.plugin.lang.psi.RsTokenTypes.MULTI_LINE_COMMENT
import io.runescript.plugin.lang.psi.RsTokenTypes.SINGLE_LINE_COMMENT
import io.runescript.plugin.lang.psi.RsTypes.*

object RsTokenTypesSets {
    val KEYWORDS = TokenSet.create(
        IF,
        WHILE,
        SWITCH,
        CASE,
        DEFAULT,
        RETURN,
        DEFINE_TYPE,
        CALC,
        RETURN,
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
        SINGLE_LINE_COMMENT,
        MULTI_LINE_COMMENT,
    )

    val STRING_ELEMENTS = TokenSet.create(STRING_START, STRING_PART, STRING_TAG, STRING_END)
}
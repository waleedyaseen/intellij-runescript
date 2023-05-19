package io.runescript.plugin.lang.psi

import com.intellij.psi.TokenType
import com.intellij.psi.tree.TokenSet
import io.runescript.plugin.lang.psi.RuneScriptTypes.*

object RuneScriptTokenTypesSets {
    val WHITE_SPACE = TokenType.WHITE_SPACE
    val BAD_CHARACTER = TokenType.BAD_CHARACTER
    val KEYWORDS = TokenSet.create(
        IF,
        WHILE,
        SWITCH,
        SWITCH_CASE,
        RETURN,
        DEFINE_TYPE,
        TYPE_NAME,
        ARRAY_TYPE_NAME,
        CALC,
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
}
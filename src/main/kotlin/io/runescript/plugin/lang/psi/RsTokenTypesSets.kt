package io.runescript.plugin.lang.psi

import com.intellij.psi.tree.TokenSet
import io.runescript.plugin.lang.psi.RsElementTypes.AMPERSAND
import io.runescript.plugin.lang.psi.RsElementTypes.BAR
import io.runescript.plugin.lang.psi.RsElementTypes.CALC
import io.runescript.plugin.lang.psi.RsElementTypes.CASE
import io.runescript.plugin.lang.psi.RsElementTypes.DEFAULT
import io.runescript.plugin.lang.psi.RsElementTypes.DEFINE_TYPE
import io.runescript.plugin.lang.psi.RsElementTypes.ELSE
import io.runescript.plugin.lang.psi.RsElementTypes.EQUAL
import io.runescript.plugin.lang.psi.RsElementTypes.EXCEL
import io.runescript.plugin.lang.psi.RsElementTypes.FALSE
import io.runescript.plugin.lang.psi.RsElementTypes.GT
import io.runescript.plugin.lang.psi.RsElementTypes.GTE
import io.runescript.plugin.lang.psi.RsElementTypes.IF
import io.runescript.plugin.lang.psi.RsElementTypes.LBRACE
import io.runescript.plugin.lang.psi.RsElementTypes.LBRACKET
import io.runescript.plugin.lang.psi.RsElementTypes.LPAREN
import io.runescript.plugin.lang.psi.RsElementTypes.LT
import io.runescript.plugin.lang.psi.RsElementTypes.LTE
import io.runescript.plugin.lang.psi.RsElementTypes.MINUS
import io.runescript.plugin.lang.psi.RsElementTypes.NULL
import io.runescript.plugin.lang.psi.RsElementTypes.PERCENT
import io.runescript.plugin.lang.psi.RsElementTypes.PLUS
import io.runescript.plugin.lang.psi.RsElementTypes.RBRACE
import io.runescript.plugin.lang.psi.RsElementTypes.RBRACKET
import io.runescript.plugin.lang.psi.RsElementTypes.RETURN
import io.runescript.plugin.lang.psi.RsElementTypes.RPAREN
import io.runescript.plugin.lang.psi.RsElementTypes.SLASH
import io.runescript.plugin.lang.psi.RsElementTypes.STAR
import io.runescript.plugin.lang.psi.RsElementTypes.STRING_END
import io.runescript.plugin.lang.psi.RsElementTypes.STRING_PART
import io.runescript.plugin.lang.psi.RsElementTypes.STRING_START
import io.runescript.plugin.lang.psi.RsElementTypes.STRING_TAG
import io.runescript.plugin.lang.psi.RsElementTypes.SWITCH
import io.runescript.plugin.lang.psi.RsElementTypes.TRUE
import io.runescript.plugin.lang.psi.RsElementTypes.WHILE
import io.runescript.plugin.lang.psi.RsTokenTypes.BLOCK_COMMENT
import io.runescript.plugin.lang.psi.RsTokenTypes.DOC_COMMENT
import io.runescript.plugin.lang.psi.RsTokenTypes.LINE_COMMENT

object RsTokenTypesSets {
    val KEYWORDS =
        TokenSet.create(
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
            NULL,
        )
    val OPERATORS =
        TokenSet.create(
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
    val BRACES =
        TokenSet.create(
            LBRACE,
            RBRACE,
        )
    val BRACKETS =
        TokenSet.create(
            LBRACKET,
            RBRACKET,
        )
    val PARENS =
        TokenSet.create(
            LPAREN,
            RPAREN,
        )
    val COMMENTS =
        TokenSet.create(
            LINE_COMMENT,
            BLOCK_COMMENT,
            DOC_COMMENT,
        )

    val STRING_ELEMENTS = TokenSet.create(STRING_START, STRING_PART, STRING_TAG, STRING_END)
}

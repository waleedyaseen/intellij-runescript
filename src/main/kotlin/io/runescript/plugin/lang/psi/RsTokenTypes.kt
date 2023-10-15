package io.runescript.plugin.lang.psi

import com.intellij.psi.tree.ILazyParseableElementType
import com.intellij.psi.tree.TokenSet
import io.runescript.plugin.lang.doc.lexer.RsDocTokens


object RsTokenTypes {
    val ID_TOKENS = TokenSet.create(
        RsElementTypes.IDENTIFIER,
        RsElementTypes.DEFINE_TYPE,
        RsElementTypes.TYPE_LITERAL,
        RsElementTypes.ARRAY_TYPE_LITERAL,
        RsElementTypes.WHILE,
        RsElementTypes.IF,
        RsElementTypes.TRUE,
        RsElementTypes.FALSE,
        RsElementTypes.NULL,
        RsElementTypes.SWITCH
    )

    @JvmField
    val LINE_COMMENT = RsElementType("LINE_COMMENT")

    @JvmField
    val BLOCK_COMMENT = RsElementType("BLOCK_COMMENT")

    @JvmField
    val DOC_COMMENT: ILazyParseableElementType = RsDocTokens.RSDOC
}
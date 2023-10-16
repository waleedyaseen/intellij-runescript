package io.runescript.plugin.lang.parser

import com.intellij.lang.WhitespacesAndCommentsBinder
import com.intellij.lang.parser.GeneratedParserUtilBase
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.TokenType
import io.runescript.plugin.lang.psi.RsTokenTypes

object RsParserUtil : GeneratedParserUtilBase() {

    @JvmField
    val SCRIPT_LEFT_BINDER = WhitespacesAndCommentsBinder { tokens, _, _ ->
        for (idx in tokens.indices.reversed()) {
            if (tokens[idx] == RsTokenTypes.DOC_COMMENT) {
                return@WhitespacesAndCommentsBinder idx
            }
        }
        return@WhitespacesAndCommentsBinder tokens.size
    }

    @JvmField
    val SCRIPT_RIGHT_BINDER = WhitespacesAndCommentsBinder { tokens, _, getter ->
        if (tokens.isEmpty()) return@WhitespacesAndCommentsBinder 0

        var result = 0
        tokens@ for (idx in tokens.indices) {
            val tokenType = tokens[idx]
            when (tokenType) {
                TokenType.WHITE_SPACE -> if (StringUtil.containsLineBreak(getter[idx])) break@tokens
                RsTokenTypes.LINE_COMMENT, RsTokenTypes.BLOCK_COMMENT -> result = idx + 1
                else -> break@tokens
            }
        }

        return@WhitespacesAndCommentsBinder result
    }
}
package io.runescript.plugin.ide.highlight

import com.intellij.lexer.LayeredLexer
import com.intellij.psi.tree.IElementType
import io.runescript.plugin.lang.doc.lexer.RsDocLexer
import io.runescript.plugin.lang.lexer.RsLexerAdapter
import io.runescript.plugin.lang.lexer.RsLexerInfo
import io.runescript.plugin.lang.psi.RsTokenTypes

class RsHighlightingLexer(lexerInfo: RsLexerInfo) : LayeredLexer(RsLexerAdapter(lexerInfo)) {
    init {
        registerSelfStoppingLayer(RsDocLexer(), arrayOf(RsTokenTypes.DOC_COMMENT), IElementType.EMPTY_ARRAY)
    }
}
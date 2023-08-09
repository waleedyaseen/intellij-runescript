package io.runescript.plugin.lang.lexer

import com.intellij.lexer.FlexAdapter
import com.intellij.lexer.MergingLexerAdapter
import com.intellij.psi.tree.TokenSet
import io.runescript.plugin.lang.psi.RsElementTypes

class RsLexerAdapter(lexerInfo: RsLexerInfo) : MergingLexerAdapter(
    FlexAdapter(_RsLexer(null, lexerInfo)), TokenSet.create(RsElementTypes.STRING_PART)
)
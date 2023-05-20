package io.runescript.plugin.lang.lexer

import com.intellij.lexer.FlexAdapter
import com.intellij.lexer.MergingLexerAdapter
import com.intellij.psi.tree.TokenSet

class RsLexerAdapter(lexerInfo: RsLexerInfo) : MergingLexerAdapter(FlexAdapter(_RsLexer(null, lexerInfo)), TokenSet.WHITE_SPACE)
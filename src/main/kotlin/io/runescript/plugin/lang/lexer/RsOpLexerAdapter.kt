package io.runescript.plugin.lang.lexer

import com.intellij.lexer.FlexAdapter

class RsOpLexerAdapter(lexerInfo: RsLexerInfo) : FlexAdapter(_RsOpLexer(null, lexerInfo))
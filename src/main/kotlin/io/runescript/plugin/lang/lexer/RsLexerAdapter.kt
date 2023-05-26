package io.runescript.plugin.lang.lexer

import com.intellij.lexer.FlexAdapter

class RsLexerAdapter(lexerInfo: RsLexerInfo) : FlexAdapter(_RsLexer(null, lexerInfo))
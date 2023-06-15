package io.runescript.plugin.oplang.lexer

import com.intellij.lexer.FlexAdapter
import io.runescript.plugin.lang.lexer.RsLexerInfo

class RsOpLexer(lexerInfo: RsLexerInfo) : FlexAdapter(_RsOpLexer(null, lexerInfo))
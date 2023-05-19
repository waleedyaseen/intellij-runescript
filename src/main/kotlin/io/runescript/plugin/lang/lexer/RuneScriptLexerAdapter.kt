package io.runescript.plugin.lang.lexer

import com.intellij.lexer.FlexAdapter
import com.intellij.lexer.MergingLexerAdapter
import com.intellij.psi.tree.TokenSet

class RuneScriptLexerAdapter(lexerInfo: RuneScriptLexerInfo) : MergingLexerAdapter(FlexAdapter(_RuneScriptLexer(null, lexerInfo)), TokenSet.WHITE_SPACE)
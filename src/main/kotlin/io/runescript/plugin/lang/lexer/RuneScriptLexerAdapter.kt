package io.runescript.plugin.lang.lexer

import com.intellij.lexer.FlexAdapter
import com.intellij.lexer.MergingLexerAdapter
import com.intellij.openapi.project.Project
import com.intellij.psi.tree.TokenSet

class RuneScriptLexerAdapter(project: Project) : MergingLexerAdapter(FlexAdapter(_RuneScriptLexer(null, project)), TokenSet.WHITE_SPACE)
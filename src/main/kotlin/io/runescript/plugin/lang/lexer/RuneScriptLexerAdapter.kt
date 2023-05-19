package io.runescript.plugin.lang.lexer

import com.intellij.lexer.FlexAdapter
import com.intellij.openapi.project.Project

class RuneScriptLexerAdapter(project: Project) : FlexAdapter(_RuneScriptLexer(null, project))
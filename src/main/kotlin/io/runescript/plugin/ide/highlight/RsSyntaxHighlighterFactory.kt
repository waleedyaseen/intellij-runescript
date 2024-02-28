package io.runescript.plugin.ide.highlight

import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import io.runescript.plugin.ide.config.RsConfig
import io.runescript.plugin.lang.lexer.RsLexerInfo

class RsSyntaxHighlighterFactory : SyntaxHighlighterFactory() {

    override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?): SyntaxHighlighter {
        return RsSyntaxHighlighter(RsLexerInfo(RsConfig.getPrimitiveTypes()))
    }
}
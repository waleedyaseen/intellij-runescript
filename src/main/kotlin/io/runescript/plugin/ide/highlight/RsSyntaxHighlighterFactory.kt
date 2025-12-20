package io.runescript.plugin.ide.highlight

import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import io.runescript.plugin.ide.neptune.DEFAULT_RESOLVED_DATA
import io.runescript.plugin.ide.neptune.neptuneModuleData
import io.runescript.plugin.lang.lexer.RsLexerInfo

class RsSyntaxHighlighterFactory : SyntaxHighlighterFactory() {

    override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?): SyntaxHighlighter {
        val resolvedData = if (project != null && virtualFile != null) {
            val module = ModuleUtilCore.findModuleForFile(virtualFile, project)
            module?.neptuneModuleData?.resolvedData
        } else {
            null
        }
        val types = (resolvedData ?: DEFAULT_RESOLVED_DATA).types
        return RsSyntaxHighlighter(RsLexerInfo(types))
    }
}
package io.runescript.plugin.ide.highlight

import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import io.runescript.plugin.ide.neptune.DEFAULT_TYPE_MANAGER
import io.runescript.plugin.ide.neptune.neptuneModuleData
import io.runescript.plugin.lang.lexer.RsLexerInfo

class RsSyntaxHighlighterFactory : SyntaxHighlighterFactory() {

    override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?): SyntaxHighlighter {
        val types = if (project != null && virtualFile != null) {
            val module = ModuleUtilCore.findModuleForFile(virtualFile, project)
            module?.neptuneModuleData?.types
        } else {
            null
        } ?: DEFAULT_TYPE_MANAGER
        return RsSyntaxHighlighter(RsLexerInfo(types))
    }
}
package io.runescript.plugin.lang.parser

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import io.runescript.plugin.ide.config.RsConfig
import io.runescript.plugin.lang.RuneScript
import io.runescript.plugin.lang.lexer.RuneScriptLexerAdapter
import io.runescript.plugin.lang.lexer.RuneScriptLexerInfo
import io.runescript.plugin.lang.psi.RuneScriptFile
import io.runescript.plugin.lang.psi.RuneScriptTokenTypesSets
import io.runescript.plugin.lang.psi.RuneScriptTypes

class RuneScriptParserDefinition : ParserDefinition {

    private val FILE = IFileElementType(RuneScript)

    override fun createLexer(project: Project): Lexer {
        return RuneScriptLexerAdapter(RuneScriptLexerInfo(RsConfig.getPrimitiveTypes(project)))
    }

    override fun createParser(project: Project): PsiParser {
        return RuneScriptParser()
    }

    override fun getFileNodeType(): IFileElementType {
        return FILE
    }

    override fun getCommentTokens(): TokenSet {
        return RuneScriptTokenTypesSets.COMMENTS
    }

    override fun getStringLiteralElements(): TokenSet {
        return RuneScriptTokenTypesSets.STRING_ELEMENTS
    }

    override fun createElement(node: ASTNode?): PsiElement {
        return RuneScriptTypes.Factory.createElement(node)
    }

    override fun createFile(viewProvider: FileViewProvider): PsiFile {
        return RuneScriptFile(viewProvider)
    }
}
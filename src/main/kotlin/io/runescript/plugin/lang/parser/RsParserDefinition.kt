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
import io.runescript.plugin.lang.lexer.RsLexerAdapter
import io.runescript.plugin.lang.lexer.RsLexerInfo
import io.runescript.plugin.lang.psi.RsFile
import io.runescript.plugin.lang.psi.RsTokenTypesSets
import io.runescript.plugin.lang.psi.RsTypes

class RsParserDefinition : ParserDefinition {

    private val FILE = IFileElementType(RuneScript)

    override fun createLexer(project: Project): Lexer {
        return RsLexerAdapter(RsLexerInfo(RsConfig.getPrimitiveTypes(project)))
    }

    override fun createParser(project: Project): PsiParser {
        return RsParser()
    }

    override fun getFileNodeType(): IFileElementType {
        return FILE
    }

    override fun getCommentTokens(): TokenSet {
        return RsTokenTypesSets.COMMENTS
    }

    override fun getStringLiteralElements(): TokenSet {
        return RsTokenTypesSets.STRING_ELEMENTS
    }

    override fun createElement(node: ASTNode?): PsiElement {
        return RsTypes.Factory.createElement(node)
    }

    override fun createFile(viewProvider: FileViewProvider): PsiFile {
        return RsFile(viewProvider)
    }
}
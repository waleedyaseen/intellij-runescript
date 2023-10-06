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
import io.runescript.plugin.lang.psi.RsElementTypes
import io.runescript.plugin.lang.psi.RsFile
import io.runescript.plugin.lang.psi.RsTokenTypesSets
import io.runescript.plugin.lang.stubs.types.RsFileStubType

class RsParserDefinition : ParserDefinition {

    override fun createLexer(project: Project): Lexer {
        error("Should not be called")
    }

    override fun createParser(project: Project): PsiParser {
        error("Should not be called")
    }

    override fun getFileNodeType(): IFileElementType {
        return RsFileStubType
    }

    override fun getCommentTokens(): TokenSet {
        return RsTokenTypesSets.COMMENTS
    }

    override fun getStringLiteralElements(): TokenSet {
        return RsTokenTypesSets.STRING_ELEMENTS
    }

    override fun createElement(node: ASTNode?): PsiElement {
        return RsElementTypes.Factory.createElement(node)
    }

    override fun createFile(viewProvider: FileViewProvider): PsiFile {
        return RsFile(viewProvider)
    }
}
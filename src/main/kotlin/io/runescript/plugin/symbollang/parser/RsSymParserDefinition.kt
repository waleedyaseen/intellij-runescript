package io.runescript.plugin.symbollang.parser

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
import io.runescript.plugin.symbollang.lexer.RsSymLexer
import io.runescript.plugin.symbollang.psi.RsSymElementTypes
import io.runescript.plugin.symbollang.psi.RsSymFile
import io.runescript.plugin.symbollang.psi.stub.types.RsSymFileStubType

class RsSymParserDefinition : ParserDefinition {

    override fun createLexer(project: Project?): Lexer {
        return RsSymLexer()
    }

    override fun createParser(project: Project?): PsiParser {
        return RsSymParser()
    }

    override fun getFileNodeType(): IFileElementType {
        return RsSymFileStubType
    }

    override fun getCommentTokens(): TokenSet {
        return TokenSet.EMPTY
    }

    override fun getStringLiteralElements(): TokenSet {
        return TokenSet.EMPTY
    }

    override fun createElement(node: ASTNode?): PsiElement {
        return RsSymElementTypes.Factory.createElement(node)
    }

    override fun createFile(viewProvider: FileViewProvider): PsiFile {
        return RsSymFile(viewProvider)
    }
}
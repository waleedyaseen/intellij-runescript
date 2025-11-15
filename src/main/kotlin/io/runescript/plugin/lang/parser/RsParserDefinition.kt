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
import io.runescript.plugin.ide.neptune.typeManagerOrDefault
import io.runescript.plugin.lang.doc.lexer.RsDocTokens
import io.runescript.plugin.lang.doc.parser.RsDocElementType
import io.runescript.plugin.lang.doc.psi.impl.RsDocLink
import io.runescript.plugin.lang.lexer.RsLexerAdapter
import io.runescript.plugin.lang.lexer.RsLexerInfo
import io.runescript.plugin.lang.psi.RsElementTypes
import io.runescript.plugin.lang.psi.RsFile
import io.runescript.plugin.lang.psi.RsTokenTypesSets
import io.runescript.plugin.lang.stubs.types.RsFileStubType

class RsParserDefinition : ParserDefinition {

    override fun createLexer(project: Project): Lexer {
        val typeManager = project.typeManagerOrDefault
        return RsLexerAdapter(RsLexerInfo(typeManager))
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

    override fun createElement(node: ASTNode): PsiElement {
        return when (val elementType = node.elementType) {
            is RsDocElementType -> elementType.createPsi(node)
            RsDocTokens.MARKDOWN_LINK -> RsDocLink(node)
            else -> RsElementTypes.Factory.createElement(node)
        }
    }

    override fun createFile(viewProvider: FileViewProvider): PsiFile {
        return RsFile(viewProvider)
    }
}
package io.runescript.plugin.ide.formatter.lineIndent

import com.intellij.formatting.Indent
import com.intellij.lang.Language
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.TokenType
import com.intellij.psi.impl.source.codeStyle.SemanticEditorPosition
import com.intellij.psi.impl.source.codeStyle.lineIndent.IndentCalculator
import com.intellij.psi.impl.source.codeStyle.lineIndent.JavaLikeLangLineIndentProvider
import com.intellij.psi.impl.source.codeStyle.lineIndent.JavaLikeLangLineIndentProvider.JavaLikeElement.*
import com.intellij.psi.tree.IElementType
import io.runescript.plugin.lang.RuneScript
import io.runescript.plugin.lang.doc.lexer.RsDocTokens
import io.runescript.plugin.lang.psi.RsElementTypes
import io.runescript.plugin.lang.psi.RsTokenTypes

class RsLineIndentProvider : JavaLikeLangLineIndentProvider() {
    override fun mapType(tokenType: IElementType): SemanticEditorPosition.SyntaxElement? {
        return elementsMap[tokenType]
    }

    override fun getIndent(project: Project, editor: Editor, language: Language?, offset: Int): IndentCalculator? {
        val currentPosition = getPosition(editor, offset)
        val before = currentPosition.beforeOptionalMix(Whitespace, LineComment, BlockComment)
        val factory = IndentCalculatorFactory(project, editor)
        if (getPosition(editor, offset).before().isAt(LeftParenthesis)) {
            return null
        }
        return when {
            before.isAt(Semicolon) -> factory.createIndentCalculator(Indent.getNoneIndent(), IndentCalculator.LINE_BEFORE)
            before.isAt(BlockOpeningBrace) -> factory.createIndentCalculator(Indent.getNormalIndent()) { before.startOffset }
            else -> super.getIndent(project, editor, language, offset)
        }
    }

    override fun isSuitableForLanguage(language: Language) = language.isKindOf(RuneScript)

    companion object {
        private val elementsMap: Map<IElementType, SemanticEditorPosition.SyntaxElement> = hashMapOf(
            TokenType.WHITE_SPACE to Whitespace,
            RsElementTypes.SEMICOLON to Semicolon,
            RsElementTypes.LBRACE to BlockOpeningBrace,
            RsElementTypes.RBRACE to BlockClosingBrace,
            RsElementTypes.LPAREN to RightParenthesis,
            RsElementTypes.RPAREN to LeftParenthesis,
            RsElementTypes.COLON to Colon,
            RsElementTypes.CASE to SwitchCase,
            RsElementTypes.DEFAULT to SwitchDefault,
            RsElementTypes.ELSE to ElseKeyword,
            RsElementTypes.IF to IfKeyword,
            RsTokenTypes.BLOCK_COMMENT to BlockComment,
            RsTokenTypes.LINE_COMMENT to LineComment,
            RsDocTokens.START to DocBlockStart,
            RsDocTokens.END to DocBlockEnd,
            RsElementTypes.COMMA to Comma
        )
    }
}
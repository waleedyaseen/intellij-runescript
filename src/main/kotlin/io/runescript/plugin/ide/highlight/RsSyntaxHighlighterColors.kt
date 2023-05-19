package io.runescript.plugin.ide.highlight

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey


object RsSyntaxHighlighterColors {
    // Lexer based attribute keys
    private const val RUNESCRIPT_IDENTIFIER = "RUNESCRIPT_IDENTIFIER"
    private const val RUNESCRIPT_NUMBER = "RUNESCRIPT_NUMBER"
    private const val RUNESCRIPT_KEYWORD = "RUNESCRIPT_KEYWORD"
    private const val RUNESCRIPT_STRING = "RUNESCRIPT_STRING"
    private const val RUNESCRIPT_BLOCK_COMMENT = "RUNESCRIPT_BLOCK_COMMENT"
    private const val RUNESCRIPT_LINE_COMMENT = "RUNESCRIPT_LINE_COMMENT"
    private const val RUNESCRIPT_OPERATION_SIGN = "RUNESCRIPT_OPERATION_SIGN"
    private const val RUNESCRIPT_BRACES = "RUNESCRIPT_BRACES"
    private const val RUNESCRIPT_SEMICOLON = "RUNESCRIPT_SEMICOLON"
    private const val RUNESCRIPT_COMMA = "RUNESCRIPT_COMMA"
    private const val RUNESCRIPT_PARENTHESIS = "RUNESCRIPT_PARENTHESIS"
    private const val RUNESCRIPT_BRACKETS = "RUNESCRIPT_BRACKETS"

    // Parser based attribute keys
    private const val RUNESCRIPT_LOCAL_VARIABLE = "RUNESCRIPT_LOCAL_VARIABLE"
    private const val RUNESCRIPT_SCOPED_VARIABLE = "RUNESCRIPT_SCOPED_VARIABLE"
    private const val RUNESCRIPT_CONSTANT = "RUNESCRIPT_CONSTANT"

    // Lexer based attributes
    val IDENTIFIER = createTextAttributesKey(RUNESCRIPT_IDENTIFIER, DefaultLanguageHighlighterColors.IDENTIFIER)
    val NUMBER = createTextAttributesKey(RUNESCRIPT_NUMBER, DefaultLanguageHighlighterColors.NUMBER)
    val KEYWORD = createTextAttributesKey(RUNESCRIPT_KEYWORD, DefaultLanguageHighlighterColors.KEYWORD)
    val STRING = createTextAttributesKey(RUNESCRIPT_STRING, DefaultLanguageHighlighterColors.STRING)
    val BLOCK_COMMENT = createTextAttributesKey(RUNESCRIPT_BLOCK_COMMENT, DefaultLanguageHighlighterColors.BLOCK_COMMENT)
    val LINE_COMMENT = createTextAttributesKey(RUNESCRIPT_LINE_COMMENT, DefaultLanguageHighlighterColors.LINE_COMMENT)
    val OPERATION_SIGN = createTextAttributesKey(RUNESCRIPT_OPERATION_SIGN, DefaultLanguageHighlighterColors.OPERATION_SIGN)
    val BRACES = createTextAttributesKey(RUNESCRIPT_BRACES, DefaultLanguageHighlighterColors.BRACES)
    val SEMICOLON = createTextAttributesKey(RUNESCRIPT_SEMICOLON, DefaultLanguageHighlighterColors.SEMICOLON)
    val COMMA = createTextAttributesKey(RUNESCRIPT_COMMA, DefaultLanguageHighlighterColors.COMMA)
    val PARENTHESIS = createTextAttributesKey(RUNESCRIPT_PARENTHESIS, DefaultLanguageHighlighterColors.PARENTHESES)
    val BRACKETS = createTextAttributesKey(RUNESCRIPT_BRACKETS, DefaultLanguageHighlighterColors.BRACKETS)

    // Parser based attributes
    val CONSTANT = createTextAttributesKey(RUNESCRIPT_CONSTANT, DefaultLanguageHighlighterColors.CONSTANT)
    val LOCAL_VARIABLE = createTextAttributesKey(RUNESCRIPT_LOCAL_VARIABLE, DefaultLanguageHighlighterColors.LOCAL_VARIABLE)
    val SCOPED_VARIABLE = createTextAttributesKey(RUNESCRIPT_SCOPED_VARIABLE, DefaultLanguageHighlighterColors.GLOBAL_VARIABLE)
}
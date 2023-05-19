package io.runescript.plugin.ide.highlight

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey


object RsSyntaxHighlighterColors {
    // Lexer based attribute keys
    const val RUNESCRIPT_IDENTIFIER = "RUNESCRIPT_IDENTIFIER"
    const val RUNESCRIPT_NUMBER = "RUNESCRIPT_NUMBER"
    const val RUNESCRIPT_KEYWORD = "RUNESCRIPT_KEYWORD"
    const val RUNESCRIPT_TYPE_NAME = "RUNESCRIPT_TYPE_NAME"
    const val RUNESCRIPT_ARRAY_TYPE_NAME = "RUNESCRIPT_ARRAY_TYPE_NAME"
    const val RUNESCRIPT_STRING = "RUNESCRIPT_STRING"
    const val RUNESCRIPT_STRING_TAG = "RUNESCRIPT_STRING_TAG"
    const val RUNESCRIPT_BLOCK_COMMENT = "RUNESCRIPT_BLOCK_COMMENT"
    const val RUNESCRIPT_LINE_COMMENT = "RUNESCRIPT_LINE_COMMENT"
    const val RUNESCRIPT_OPERATION_SIGN = "RUNESCRIPT_OPERATION_SIGN"
    const val RUNESCRIPT_BRACES = "RUNESCRIPT_BRACES"
    const val RUNESCRIPT_SEMICOLON = "RUNESCRIPT_SEMICOLON"
    const val RUNESCRIPT_COMMA = "RUNESCRIPT_COMMA"
    const val RUNESCRIPT_PARENTHESIS = "RUNESCRIPT_PARENTHESIS"
    const val RUNESCRIPT_BRACKETS = "RUNESCRIPT_BRACKETS"

    // Parser based attribute keys
    private const val RUNESCRIPT_LOCAL_VARIABLE = "RUNESCRIPT_LOCAL_VARIABLE"
    private const val RUNESCRIPT_SCOPED_VARIABLE = "RUNESCRIPT_SCOPED_VARIABLE"
    private const val RUNESCRIPT_CONSTANT = "RUNESCRIPT_CONSTANT"

    // Lexer based attributes
    val IDENTIFIER = createTextAttributesKey(RUNESCRIPT_IDENTIFIER, DefaultLanguageHighlighterColors.IDENTIFIER)
    val NUMBER = createTextAttributesKey(RUNESCRIPT_NUMBER, DefaultLanguageHighlighterColors.NUMBER)
    val KEYWORD = createTextAttributesKey(RUNESCRIPT_KEYWORD, DefaultLanguageHighlighterColors.KEYWORD)
    val TYPE_NAME = createTextAttributesKey(RUNESCRIPT_TYPE_NAME, DefaultLanguageHighlighterColors.CLASS_NAME)
    val ARRAY_TYPE_NAME = createTextAttributesKey(RUNESCRIPT_ARRAY_TYPE_NAME, DefaultLanguageHighlighterColors.CLASS_NAME)
    val STRING = createTextAttributesKey(RUNESCRIPT_STRING, DefaultLanguageHighlighterColors.STRING)
    val STRING_TAG = createTextAttributesKey(RUNESCRIPT_STRING_TAG, DefaultLanguageHighlighterColors.STRING)
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
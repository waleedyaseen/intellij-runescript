package io.runescript.plugin.ide.highlight

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey


object RsSyntaxHighlighterColors {
    // Lexer based attribute keys
    private const val RUNESCRIPT_IDENTIFIER = "RUNESCRIPT_IDENTIFIER"
    private const val RUNESCRIPT_NUMBER = "RUNESCRIPT_NUMBER"
    private const val RUNESCRIPT_KEYWORD = "RUNESCRIPT_KEYWORD"
    private const val RUNESCRIPT_TYPE_LITERAL = "RUNESCRIPT_TYPE_LITERAL"
    private const val RUNESCRIPT_STRING = "RUNESCRIPT_STRING"
    private const val RUNESCRIPT_STRING_TAG = "RUNESCRIPT_STRING_TAG"
    private const val RUNESCRIPT_BLOCK_COMMENT = "RUNESCRIPT_BLOCK_COMMENT"
    private const val RUNESCRIPT_LINE_COMMENT = "RUNESCRIPT_LINE_COMMENT"
    private const val RUNESCRIPT_OPERATION_SIGN = "RUNESCRIPT_OPERATION_SIGN"
    private const val RUNESCRIPT_BRACES = "RUNESCRIPT_BRACES"
    private const val RUNESCRIPT_SEMICOLON = "RUNESCRIPT_SEMICOLON"
    private const val RUNESCRIPT_COMMA = "RUNESCRIPT_COMMA"
    private const val RUNESCRIPT_PARENTHESIS = "RUNESCRIPT_PARENTHESIS"
    private const val RUNESCRIPT_BRACKETS = "RUNESCRIPT_BRACKETS"

    // Documentation
    private const val RUNESCRIPT_DOC_COMMENT = "RUNESCRIPT_DOC_COMMENT"
    private const val RUNESCRIPT_DOC_COMMENT_TAG = "RUNESCRIPT_DOC_COMMENT_TAG"
    private const val RUNESCRIPT_DOC_COMMENT_LINK = "RUNESCRIPT_DOC_COMMENT_LINK"

    // Parser based attribute keys
    private const val RUNESCRIPT_SCRIPT_DECLARATION = "RUNESCRIPT_SCRIPT_DECLARATION"
    private const val RUNESCRIPT_CONSTANT = "RUNESCRIPT_CONSTANT"
    private const val RUNESCRIPT_LOCAL_VARIABLE = "RUNESCRIPT_LOCAL_VARIABLE"
    private const val RUNESCRIPT_SCOPED_VARIABLE = "RUNESCRIPT_SCOPED_VARIABLE"
    private const val RUNESCRIPT_COMMAND_CALL = "RUNESCRIPT_COMMAND_CALL"
    private const val RUNESCRIPT_PROC_CALL = "RUNESCRIPT_PROC_CALL"
    private const val RUNESCRIPT_CLIENTSCRIPT_CALL = "RUNESCRIPT_CLIENTSCRIPT_CALL"
    private const val RUNESCRIPT_CONFIG_REFERENCE = "RUNESCRIPT_CONFIG_REFERENCE"

    // Lexer based attributes
    val IDENTIFIER = createTextAttributesKey(RUNESCRIPT_IDENTIFIER, DefaultLanguageHighlighterColors.IDENTIFIER)
    val NUMBER = createTextAttributesKey(RUNESCRIPT_NUMBER, DefaultLanguageHighlighterColors.NUMBER)
    val KEYWORD = createTextAttributesKey(RUNESCRIPT_KEYWORD, DefaultLanguageHighlighterColors.KEYWORD)
    val TYPE_LITERAL = createTextAttributesKey(RUNESCRIPT_TYPE_LITERAL, DefaultLanguageHighlighterColors.KEYWORD)
    val STRING = createTextAttributesKey(RUNESCRIPT_STRING, DefaultLanguageHighlighterColors.STRING)
    val STRING_TAG = createTextAttributesKey(RUNESCRIPT_STRING_TAG, DefaultLanguageHighlighterColors.MARKUP_ENTITY)
    val BLOCK_COMMENT = createTextAttributesKey(RUNESCRIPT_BLOCK_COMMENT, DefaultLanguageHighlighterColors.BLOCK_COMMENT)
    val LINE_COMMENT = createTextAttributesKey(RUNESCRIPT_LINE_COMMENT, DefaultLanguageHighlighterColors.LINE_COMMENT)
    val OPERATION_SIGN = createTextAttributesKey(RUNESCRIPT_OPERATION_SIGN, DefaultLanguageHighlighterColors.OPERATION_SIGN)
    val BRACES = createTextAttributesKey(RUNESCRIPT_BRACES, DefaultLanguageHighlighterColors.BRACES)
    val SEMICOLON = createTextAttributesKey(RUNESCRIPT_SEMICOLON, DefaultLanguageHighlighterColors.SEMICOLON)
    val COLON = createTextAttributesKey(RUNESCRIPT_SEMICOLON, DefaultLanguageHighlighterColors.SEMICOLON)
    val COMMA = createTextAttributesKey(RUNESCRIPT_COMMA, DefaultLanguageHighlighterColors.COMMA)
    val PARENTHESIS = createTextAttributesKey(RUNESCRIPT_PARENTHESIS, DefaultLanguageHighlighterColors.PARENTHESES)
    val BRACKETS = createTextAttributesKey(RUNESCRIPT_BRACKETS, DefaultLanguageHighlighterColors.BRACKETS)

    // Documentation
    val DOC_COMMENT = createTextAttributesKey(RUNESCRIPT_DOC_COMMENT, DefaultLanguageHighlighterColors.DOC_COMMENT)
    val DOC_COMMENT_TAG = createTextAttributesKey(RUNESCRIPT_DOC_COMMENT_TAG, DefaultLanguageHighlighterColors.DOC_COMMENT_TAG)
    val DOC_COMMENT_LINK = createTextAttributesKey(RUNESCRIPT_DOC_COMMENT_LINK, DefaultLanguageHighlighterColors.DOC_COMMENT_TAG_VALUE)

    // Parser based attributes
    val SCRIPT_DECLARATION = createTextAttributesKey(RUNESCRIPT_SCRIPT_DECLARATION, DefaultLanguageHighlighterColors.FUNCTION_DECLARATION)
    val CONSTANT = createTextAttributesKey(RUNESCRIPT_CONSTANT, DefaultLanguageHighlighterColors.CONSTANT)
    val LOCAL_VARIABLE = createTextAttributesKey(RUNESCRIPT_LOCAL_VARIABLE, DefaultLanguageHighlighterColors.INSTANCE_FIELD)
    val SCOPED_VARIABLE = createTextAttributesKey(RUNESCRIPT_SCOPED_VARIABLE, DefaultLanguageHighlighterColors.STATIC_FIELD)
    val COMMAND_CALL = createTextAttributesKey(RUNESCRIPT_COMMAND_CALL, DefaultLanguageHighlighterColors.STATIC_METHOD)
    val PROC_CALL = createTextAttributesKey(RUNESCRIPT_PROC_CALL, DefaultLanguageHighlighterColors.INSTANCE_METHOD)
    val CLIENTSCRIPT_CALL = createTextAttributesKey(RUNESCRIPT_CLIENTSCRIPT_CALL, DefaultLanguageHighlighterColors.INSTANCE_METHOD)
    val CONFIG_REFERENCE = createTextAttributesKey(RUNESCRIPT_CONFIG_REFERENCE, DefaultLanguageHighlighterColors.CLASS_REFERENCE)
}
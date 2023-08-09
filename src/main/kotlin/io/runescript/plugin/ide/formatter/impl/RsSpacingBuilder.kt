package io.runescript.plugin.ide.formatter.impl

import com.intellij.formatting.Block
import com.intellij.formatting.Spacing
import com.intellij.psi.codeStyle.CommonCodeStyleSettings
import io.runescript.plugin.ide.formatter.*
import io.runescript.plugin.ide.formatter.blocks.RsBlock
import io.runescript.plugin.ide.formatter.style.RsCodeStyleSettings
import io.runescript.plugin.lang.psi.RsArgumentList
import io.runescript.plugin.lang.psi.RsElementTypes.*


class RsSpacingBuilder(private val settings: CommonCodeStyleSettings,
                       private val rsSettings: RsCodeStyleSettings) {

    fun getSpacing(parent: RsBlock, child1: Block?, child2: Block): Spacing? {
        if (child1 !is RsBlock || child2 !is RsBlock) {
            return null
        }
        val elementType = parent.node.elementType
        val type1 = child1.node.elementType
        val type2 = child2.node.elementType
        // Space around operators
        if (child1.isLogicalOperator() || child2.isLogicalOperator()) {
            return spaceIf(settings.SPACE_AROUND_LOGICAL_OPERATORS)
        }
        if (child1.isEqualityOperator() || child2.isEqualityOperator()) {
            return spaceIf(settings.SPACE_AROUND_EQUALITY_OPERATORS)
        }
        if (child1.isRelationalOperator() || child2.isRelationalOperator()) {
            return spaceIf(settings.SPACE_AROUND_RELATIONAL_OPERATORS)
        }
        if (child1.isBitwiseOperator() || child2.isBitwiseOperator()) {
            return spaceIf(settings.SPACE_AROUND_BITWISE_OPERATORS)
        }
        if (child1.isMultiplicativeOperator() || child2.isMultiplicativeOperator()) {
            return spaceIf(settings.SPACE_AROUND_MULTIPLICATIVE_OPERATORS)
        }
        if (child1.isAdditiveOperator() || child2.isAdditiveOperator()) {
            return spaceIf(settings.SPACE_AROUND_ADDITIVE_OPERATORS)
        }
        if (elementType == ASSIGNMENT_STATEMENT
                || elementType == LOCAL_VARIABLE_DECLARATION_STATEMENT) {
            if (type1 == EQUAL || type2 == EQUAL) {
                return spaceIf(settings.SPACE_AROUND_ASSIGNMENT_OPERATORS)
            }
        }
        if (type1 == DEFINE_TYPE || type1 == TYPE_NAME || type1 == ARRAY_TYPE_LITERAL) {
            return spaceIf(true)
        }
        if (type2 == SEMICOLON) {
            return spaceIf(settings.SPACE_BEFORE_SEMICOLON)
        }
        if (type1 == SEMICOLON) {
            return spaceIf(settings.SPACE_AFTER_SEMICOLON)
        }
        if ((elementType == ASSIGNMENT_STATEMENT &&  false /* was ARRAY_ASSIGNMENT_STATEMENT */)
                || elementType == ARRAY_VARIABLE_DECLARATION_STATEMENT
                || elementType == ARRAY_ACCESS_EXPRESSION) {
            if (type1 == LPAREN || type2 == RPAREN) {
                return spaceIf(rsSettings.SPACE_WITHIN_ARRAY_BOUNDS)
            }
            if (type2 == LPAREN) {
                return spaceIf(rsSettings.SPACE_BEFORE_ARRAY_BOUNDS)
            }
        }
        // Space before parentheses
        if (type2 == LPAREN) {
            if (type1 == SWITCH) {
                return spaceIf(settings.SPACE_BEFORE_SWITCH_PARENTHESES)
            }
            if (type1 == WHILE) {
                return spaceIf(settings.SPACE_BEFORE_WHILE_PARENTHESES)
            }
            if (type1 == IF) {
                return spaceIf(settings.SPACE_BEFORE_IF_PARENTHESES)
            }
            if (type1 == CALC) {
                return spaceIf(rsSettings.SPACE_BEFORE_CALC_PARENTHESES)
            }
        }
        // Space before left brace
        if (type2 == LBRACE && elementType == SWITCH_STATEMENT) {
            return spaceIf(settings.SPACE_BEFORE_SWITCH_LBRACE)
        }
        if (elementType == WHILE_STATEMENT && type2 == BLOCK_STATEMENT) {
            return spaceIf(settings.SPACE_BEFORE_WHILE_LBRACE)
        }
        if (elementType == IF_STATEMENT && type2 == BLOCK_STATEMENT) {
            return spaceIf(settings.SPACE_BEFORE_IF_LBRACE)
        }
        // Space before keywords
        if (elementType == ELSE) {
            return spaceIf(settings.SPACE_BEFORE_ELSE_KEYWORD)
        }
        // Space within
        if (elementType == IF_STATEMENT && (type1 == LPAREN || type2 == RPAREN)) {
            return spaceIf(settings.SPACE_WITHIN_IF_PARENTHESES)
        }
        if (elementType == SWITCH_STATEMENT && (type1 == LPAREN || type2 == RPAREN)) {
            return spaceIf(settings.SPACE_WITHIN_SWITCH_PARENTHESES)
        }
        if (elementType == WHILE_STATEMENT && (type1 == LPAREN || type2 == RPAREN)) {
            return spaceIf(settings.SPACE_WITHIN_WHILE_PARENTHESES)
        }
        if ((elementType == ARITHMETIC_VALUE_EXPRESSION || elementType == RELATIONAL_VALUE_EXPRESSION)
                && (type1 == LPAREN || type2 == RPAREN)) {
            return spaceIf(settings.SPACE_WITHIN_PARENTHESES)
        }
        if (elementType == CALC_EXPRESSION && (type1 == LPAREN || type2 == RPAREN)) {
            return spaceIf(rsSettings.SPACE_WITHIN_CALC_PARENTHESES)
        }
        if (elementType == ARGUMENT_LIST && (type1 == LPAREN || type2 == RPAREN)) {
            val superType = parent.node.treeParent.elementType
            if (superType == GOSUB_EXPRESSION || superType == COMMAND_EXPRESSION) {
                val emptyArguments = (parent.node.psi as RsArgumentList).expressionList.isEmpty()
                return spaceIf(if (emptyArguments) settings.SPACE_WITHIN_EMPTY_METHOD_CALL_PARENTHESES else settings.SPACE_WITHIN_METHOD_CALL_PARENTHESES)
            }
        }
        if (elementType == ARGUMENT_LIST || elementType == SWITCH_CASE
                || elementType == PARAMETER_LIST || elementType == RETURN_LIST) {
            if (type2 == COMMA) {
                return spaceIf(settings.SPACE_BEFORE_COMMA)
            } else if (type1 == COMMA) {
                return spaceIf(settings.SPACE_AFTER_COMMA)
            }
        }
        if (type2 == ARGUMENT_LIST && (elementType == GOSUB_EXPRESSION || elementType == COMMAND_EXPRESSION)) {
            return spaceIf(settings.SPACE_BEFORE_METHOD_CALL_PARENTHESES)
        }
        return null
    }

    private fun spaceIf(condition: Boolean): Spacing? {
        val spaces = if (condition) 1 else 0
        return Spacing.createSpacing(spaces, spaces, 0, settings.KEEP_LINE_BREAKS, settings.KEEP_BLANK_LINES_IN_CODE)
    }
}

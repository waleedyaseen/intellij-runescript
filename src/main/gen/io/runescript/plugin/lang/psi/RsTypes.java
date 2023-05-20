// This is a generated file. Not intended for manual editing.
package io.runescript.plugin.lang.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import io.runescript.plugin.lang.psi.impl.*;

public interface RsTypes {

  IElementType ARITHMETIC_ADDITIVE_EXPRESSION = new RsElementType("ARITHMETIC_ADDITIVE_EXPRESSION");
  IElementType ARITHMETIC_BITWISE_AND_EXPRESSION = new RsElementType("ARITHMETIC_BITWISE_AND_EXPRESSION");
  IElementType ARITHMETIC_BITWISE_OR_EXPRESSION = new RsElementType("ARITHMETIC_BITWISE_OR_EXPRESSION");
  IElementType ARITHMETIC_EXPRESSION = new RsElementType("ARITHMETIC_EXPRESSION");
  IElementType ARITHMETIC_MULTIPLICATIVE_EXPRESSION = new RsElementType("ARITHMETIC_MULTIPLICATIVE_EXPRESSION");
  IElementType ARITHMETIC_VALUE_EXPRESSION = new RsElementType("ARITHMETIC_VALUE_EXPRESSION");
  IElementType ARRAY_VARIABLE_ASSIGNMENT_STATEMENT = new RsElementType("ARRAY_VARIABLE_ASSIGNMENT_STATEMENT");
  IElementType ARRAY_VARIABLE_DECLARATION_STATEMENT = new RsElementType("ARRAY_VARIABLE_DECLARATION_STATEMENT");
  IElementType ARRAY_VARIABLE_EXPRESSION = new RsElementType("ARRAY_VARIABLE_EXPRESSION");
  IElementType BLOCK_STATEMENT = new RsElementType("BLOCK_STATEMENT");
  IElementType BOOLEAN_LITERAL_EXPRESSION = new RsElementType("BOOLEAN_LITERAL_EXPRESSION");
  IElementType CALC_EXPRESSION = new RsElementType("CALC_EXPRESSION");
  IElementType COMMAND_EXPRESSION = new RsElementType("COMMAND_EXPRESSION");
  IElementType COMPARE_EXPRESSION = new RsElementType("COMPARE_EXPRESSION");
  IElementType CONSTANT_EXPRESSION = new RsElementType("CONSTANT_EXPRESSION");
  IElementType DYNAMIC_EXPRESSION = new RsElementType("DYNAMIC_EXPRESSION");
  IElementType EXPRESSION = new RsElementType("EXPRESSION");
  IElementType EXPRESSION_STATEMENT = new RsElementType("EXPRESSION_STATEMENT");
  IElementType GOSUB_EXPRESSION = new RsElementType("GOSUB_EXPRESSION");
  IElementType IF_STATEMENT = new RsElementType("IF_STATEMENT");
  IElementType INTEGER_LITERAL_EXPRESSION = new RsElementType("INTEGER_LITERAL_EXPRESSION");
  IElementType LOCAL_VARIABLE_ASSIGNMENT_STATEMENT = new RsElementType("LOCAL_VARIABLE_ASSIGNMENT_STATEMENT");
  IElementType LOCAL_VARIABLE_DECLARATION_STATEMENT = new RsElementType("LOCAL_VARIABLE_DECLARATION_STATEMENT");
  IElementType LOCAL_VARIABLE_EXPRESSION = new RsElementType("LOCAL_VARIABLE_EXPRESSION");
  IElementType LOGICAL_AND_EXPRESSION = new RsElementType("LOGICAL_AND_EXPRESSION");
  IElementType LOGICAL_OR_EXPRESSION = new RsElementType("LOGICAL_OR_EXPRESSION");
  IElementType NAME_LITERAL = new RsElementType("NAME_LITERAL");
  IElementType NULL_LITERAL_EXPRESSION = new RsElementType("NULL_LITERAL_EXPRESSION");
  IElementType PARAMETER = new RsElementType("PARAMETER");
  IElementType PARAMETER_LIST = new RsElementType("PARAMETER_LIST");
  IElementType PAR_EXPRESSION = new RsElementType("PAR_EXPRESSION");
  IElementType RELATIONAL_VALUE_EXPRESSION = new RsElementType("RELATIONAL_VALUE_EXPRESSION");
  IElementType RETURN_LIST = new RsElementType("RETURN_LIST");
  IElementType RETURN_STATEMENT = new RsElementType("RETURN_STATEMENT");
  IElementType SCOPED_VARIABLE_ASSIGNMENT_STATEMENT = new RsElementType("SCOPED_VARIABLE_ASSIGNMENT_STATEMENT");
  IElementType SCOPED_VARIABLE_EXPRESSION = new RsElementType("SCOPED_VARIABLE_EXPRESSION");
  IElementType SCRIPT = new RsElementType("SCRIPT");
  IElementType SCRIPT_HEADER = new RsElementType("SCRIPT_HEADER");
  IElementType SCRIPT_NAME = new RsElementType("SCRIPT_NAME");
  IElementType STATEMENT = new RsElementType("STATEMENT");
  IElementType STATEMENT_LIST = new RsElementType("STATEMENT_LIST");
  IElementType STRING_INTERPOLATION_EXPRESSION = new RsElementType("STRING_INTERPOLATION_EXPRESSION");
  IElementType STRING_LITERAL_EXPRESSION = new RsElementType("STRING_LITERAL_EXPRESSION");
  IElementType SWITCH_CASE = new RsElementType("SWITCH_CASE");
  IElementType SWITCH_STATEMENT = new RsElementType("SWITCH_STATEMENT");
  IElementType WHILE_STATEMENT = new RsElementType("WHILE_STATEMENT");

  IElementType AMPERSAND = new RsElementType("AMPERSAND");
  IElementType ARRAY_TYPE_NAME = new RsElementType("ARRAY_TYPE_NAME");
  IElementType BAR = new RsElementType("BAR");
  IElementType CALC = new RsElementType("CALC");
  IElementType CARET = new RsElementType("CARET");
  IElementType CASE = new RsElementType("CASE");
  IElementType COLON = new RsElementType("COLON");
  IElementType COMMA = new RsElementType("COMMA");
  IElementType DEFAULT = new RsElementType("DEFAULT");
  IElementType DEFINE_TYPE = new RsElementType("DEFINE_TYPE");
  IElementType DOLLAR = new RsElementType("DOLLAR");
  IElementType EQUAL = new RsElementType("EQUAL");
  IElementType EXCEL = new RsElementType("EXCEL");
  IElementType FALSE = new RsElementType("FALSE");
  IElementType GT = new RsElementType("GT");
  IElementType GTE = new RsElementType("GTE");
  IElementType IDENTIFIER = new RsElementType("IDENTIFIER");
  IElementType IF = new RsElementType("IF");
  IElementType INTEGER = new RsElementType("INTEGER");
  IElementType LBRACE = new RsElementType("LBRACE");
  IElementType LBRACKET = new RsElementType("LBRACKET");
  IElementType LPAREN = new RsElementType("LPAREN");
  IElementType LT = new RsElementType("LT");
  IElementType LTE = new RsElementType("LTE");
  IElementType MINUS = new RsElementType("MINUS");
  IElementType MOD = new RsElementType("MOD");
  IElementType NULL = new RsElementType("NULL");
  IElementType PERCENT = new RsElementType("PERCENT");
  IElementType PLUS = new RsElementType("PLUS");
  IElementType RBRACE = new RsElementType("RBRACE");
  IElementType RBRACKET = new RsElementType("RBRACKET");
  IElementType RETURN = new RsElementType("RETURN");
  IElementType RPAREN = new RsElementType("RPAREN");
  IElementType SEMICOLON = new RsElementType("SEMICOLON");
  IElementType SLASH = new RsElementType("SLASH");
  IElementType STAR = new RsElementType("STAR");
  IElementType STRING_END = new RsElementType("STRING_END");
  IElementType STRING_INTERPOLATION_END = new RsElementType("STRING_INTERPOLATION_END");
  IElementType STRING_INTERPOLATION_START = new RsElementType("STRING_INTERPOLATION_START");
  IElementType STRING_PART = new RsElementType("STRING_PART");
  IElementType STRING_START = new RsElementType("STRING_START");
  IElementType STRING_TAG = new RsElementType("STRING_TAG");
  IElementType SWITCH = new RsElementType("SWITCH");
  IElementType TILDE = new RsElementType("TILDE");
  IElementType TRUE = new RsElementType("TRUE");
  IElementType TYPE_NAME = new RsElementType("TYPE_NAME");
  IElementType WHILE = new RsElementType("WHILE");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == ARITHMETIC_ADDITIVE_EXPRESSION) {
        return new RsArithmeticAdditiveExpressionImpl(node);
      }
      else if (type == ARITHMETIC_BITWISE_AND_EXPRESSION) {
        return new RsArithmeticBitwiseAndExpressionImpl(node);
      }
      else if (type == ARITHMETIC_BITWISE_OR_EXPRESSION) {
        return new RsArithmeticBitwiseOrExpressionImpl(node);
      }
      else if (type == ARITHMETIC_MULTIPLICATIVE_EXPRESSION) {
        return new RsArithmeticMultiplicativeExpressionImpl(node);
      }
      else if (type == ARITHMETIC_VALUE_EXPRESSION) {
        return new RsArithmeticValueExpressionImpl(node);
      }
      else if (type == ARRAY_VARIABLE_ASSIGNMENT_STATEMENT) {
        return new RsArrayVariableAssignmentStatementImpl(node);
      }
      else if (type == ARRAY_VARIABLE_DECLARATION_STATEMENT) {
        return new RsArrayVariableDeclarationStatementImpl(node);
      }
      else if (type == ARRAY_VARIABLE_EXPRESSION) {
        return new RsArrayVariableExpressionImpl(node);
      }
      else if (type == BLOCK_STATEMENT) {
        return new RsBlockStatementImpl(node);
      }
      else if (type == BOOLEAN_LITERAL_EXPRESSION) {
        return new RsBooleanLiteralExpressionImpl(node);
      }
      else if (type == CALC_EXPRESSION) {
        return new RsCalcExpressionImpl(node);
      }
      else if (type == COMMAND_EXPRESSION) {
        return new RsCommandExpressionImpl(node);
      }
      else if (type == COMPARE_EXPRESSION) {
        return new RsCompareExpressionImpl(node);
      }
      else if (type == CONSTANT_EXPRESSION) {
        return new RsConstantExpressionImpl(node);
      }
      else if (type == DYNAMIC_EXPRESSION) {
        return new RsDynamicExpressionImpl(node);
      }
      else if (type == EXPRESSION_STATEMENT) {
        return new RsExpressionStatementImpl(node);
      }
      else if (type == GOSUB_EXPRESSION) {
        return new RsGosubExpressionImpl(node);
      }
      else if (type == IF_STATEMENT) {
        return new RsIfStatementImpl(node);
      }
      else if (type == INTEGER_LITERAL_EXPRESSION) {
        return new RsIntegerLiteralExpressionImpl(node);
      }
      else if (type == LOCAL_VARIABLE_ASSIGNMENT_STATEMENT) {
        return new RsLocalVariableAssignmentStatementImpl(node);
      }
      else if (type == LOCAL_VARIABLE_DECLARATION_STATEMENT) {
        return new RsLocalVariableDeclarationStatementImpl(node);
      }
      else if (type == LOCAL_VARIABLE_EXPRESSION) {
        return new RsLocalVariableExpressionImpl(node);
      }
      else if (type == LOGICAL_AND_EXPRESSION) {
        return new RsLogicalAndExpressionImpl(node);
      }
      else if (type == LOGICAL_OR_EXPRESSION) {
        return new RsLogicalOrExpressionImpl(node);
      }
      else if (type == NAME_LITERAL) {
        return new RsNameLiteralImpl(node);
      }
      else if (type == NULL_LITERAL_EXPRESSION) {
        return new RsNullLiteralExpressionImpl(node);
      }
      else if (type == PARAMETER) {
        return new RsParameterImpl(node);
      }
      else if (type == PARAMETER_LIST) {
        return new RsParameterListImpl(node);
      }
      else if (type == PAR_EXPRESSION) {
        return new RsParExpressionImpl(node);
      }
      else if (type == RELATIONAL_VALUE_EXPRESSION) {
        return new RsRelationalValueExpressionImpl(node);
      }
      else if (type == RETURN_LIST) {
        return new RsReturnListImpl(node);
      }
      else if (type == RETURN_STATEMENT) {
        return new RsReturnStatementImpl(node);
      }
      else if (type == SCOPED_VARIABLE_ASSIGNMENT_STATEMENT) {
        return new RsScopedVariableAssignmentStatementImpl(node);
      }
      else if (type == SCOPED_VARIABLE_EXPRESSION) {
        return new RsScopedVariableExpressionImpl(node);
      }
      else if (type == SCRIPT) {
        return new RsScriptImpl(node);
      }
      else if (type == SCRIPT_HEADER) {
        return new RsScriptHeaderImpl(node);
      }
      else if (type == SCRIPT_NAME) {
        return new RsScriptNameImpl(node);
      }
      else if (type == STATEMENT_LIST) {
        return new RsStatementListImpl(node);
      }
      else if (type == STRING_INTERPOLATION_EXPRESSION) {
        return new RsStringInterpolationExpressionImpl(node);
      }
      else if (type == STRING_LITERAL_EXPRESSION) {
        return new RsStringLiteralExpressionImpl(node);
      }
      else if (type == SWITCH_CASE) {
        return new RsSwitchCaseImpl(node);
      }
      else if (type == SWITCH_STATEMENT) {
        return new RsSwitchStatementImpl(node);
      }
      else if (type == WHILE_STATEMENT) {
        return new RsWhileStatementImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}

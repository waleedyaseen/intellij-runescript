// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import io.runescript.plugin.lang.stubs.StubElementTypeFactory;
import io.runescript.plugin.lang.psi.impl.*;

public interface RsElementTypes {

  IElementType ARGUMENT_LIST = new RsElementType("ARGUMENT_LIST");
  IElementType ARITHMETIC_EXPRESSION = new RsElementType("ARITHMETIC_EXPRESSION");
  IElementType ARITHMETIC_OP = new RsElementType("ARITHMETIC_OP");
  IElementType ARITHMETIC_VALUE_EXPRESSION = new RsElementType("ARITHMETIC_VALUE_EXPRESSION");
  IElementType ARRAY_ACCESS_EXPRESSION = new RsElementType("ARRAY_ACCESS_EXPRESSION");
  IElementType ARRAY_VARIABLE_DECLARATION_STATEMENT = new RsElementType("ARRAY_VARIABLE_DECLARATION_STATEMENT");
  IElementType ASSIGNMENT_STATEMENT = new RsElementType("ASSIGNMENT_STATEMENT");
  IElementType BLOCK_STATEMENT = new RsElementType("BLOCK_STATEMENT");
  IElementType BOOLEAN_LITERAL_EXPRESSION = new RsElementType("BOOLEAN_LITERAL_EXPRESSION");
  IElementType CALC_EXPRESSION = new RsElementType("CALC_EXPRESSION");
  IElementType COMMAND_EXPRESSION = new RsElementType("COMMAND_EXPRESSION");
  IElementType CONDITION_EXPRESSION = new RsElementType("CONDITION_EXPRESSION");
  IElementType CONDITION_OP = new RsElementType("CONDITION_OP");
  IElementType CONSTANT_EXPRESSION = new RsElementType("CONSTANT_EXPRESSION");
  IElementType COORD_LITERAL_EXPRESSION = new RsElementType("COORD_LITERAL_EXPRESSION");
  IElementType DYNAMIC_EXPRESSION = new RsElementType("DYNAMIC_EXPRESSION");
  IElementType EXPRESSION = new RsElementType("EXPRESSION");
  IElementType EXPRESSION_STATEMENT = new RsElementType("EXPRESSION_STATEMENT");
  IElementType GOSUB_EXPRESSION = new RsElementType("GOSUB_EXPRESSION");
  IElementType HOOK_FRAGMENT = new RsElementType("HOOK_FRAGMENT");
  IElementType HOOK_ROOT = new RsElementType("HOOK_ROOT");
  IElementType HOOK_TRANSMIT_LIST = new RsElementType("HOOK_TRANSMIT_LIST");
  IElementType IF_STATEMENT = new RsElementType("IF_STATEMENT");
  IElementType INTEGER_LITERAL_EXPRESSION = new RsElementType("INTEGER_LITERAL_EXPRESSION");
  IElementType LOCAL_VARIABLE_DECLARATION_STATEMENT = new RsElementType("LOCAL_VARIABLE_DECLARATION_STATEMENT");
  IElementType LOCAL_VARIABLE_EXPRESSION = StubElementTypeFactory.create("LOCAL_VARIABLE_EXPRESSION");
  IElementType NAME_LITERAL = StubElementTypeFactory.create("NAME_LITERAL");
  IElementType NULL_LITERAL_EXPRESSION = new RsElementType("NULL_LITERAL_EXPRESSION");
  IElementType PARAMETER = StubElementTypeFactory.create("PARAMETER");
  IElementType PARAMETER_LIST = StubElementTypeFactory.create("PARAMETER_LIST");
  IElementType PAR_EXPRESSION = new RsElementType("PAR_EXPRESSION");
  IElementType RELATIONAL_VALUE_EXPRESSION = new RsElementType("RELATIONAL_VALUE_EXPRESSION");
  IElementType RETURN_LIST = StubElementTypeFactory.create("RETURN_LIST");
  IElementType RETURN_STATEMENT = new RsElementType("RETURN_STATEMENT");
  IElementType SCOPED_VARIABLE_EXPRESSION = StubElementTypeFactory.create("SCOPED_VARIABLE_EXPRESSION");
  IElementType SCRIPT = StubElementTypeFactory.create("SCRIPT");
  IElementType STATEMENT = new RsElementType("STATEMENT");
  IElementType STATEMENT_LIST = new RsElementType("STATEMENT_LIST");
  IElementType STRING_INTERPOLATION_EXPRESSION = new RsElementType("STRING_INTERPOLATION_EXPRESSION");
  IElementType STRING_LITERAL_CONTENT = new RsElementType("STRING_LITERAL_CONTENT");
  IElementType STRING_LITERAL_EXPRESSION = new RsElementType("STRING_LITERAL_EXPRESSION");
  IElementType SWITCH_CASE = new RsElementType("SWITCH_CASE");
  IElementType SWITCH_CASE_DEFAULT_EXPRESSION = new RsElementType("SWITCH_CASE_DEFAULT_EXPRESSION");
  IElementType SWITCH_STATEMENT = new RsElementType("SWITCH_STATEMENT");
  IElementType TYPE_NAME = StubElementTypeFactory.create("TYPE_NAME");
  IElementType WHILE_STATEMENT = new RsElementType("WHILE_STATEMENT");

  IElementType AMPERSAND = new RsElementType("&");
  IElementType ARRAY_TYPE_LITERAL = new RsElementType("ARRAY_TYPE_LITERAL");
  IElementType BAR = new RsElementType("|");
  IElementType CALC = new RsElementType("CALC");
  IElementType CARET = new RsElementType("^");
  IElementType CASE = new RsElementType("CASE");
  IElementType COLON = new RsElementType(":");
  IElementType COMMA = new RsElementType(",");
  IElementType COORDGRID = new RsElementType("COORDGRID");
  IElementType DEFAULT = new RsElementType("DEFAULT");
  IElementType DEFINE_TYPE = new RsElementType("DEFINE_TYPE");
  IElementType DOLLAR = new RsElementType("$");
  IElementType ELSE = new RsElementType("ELSE");
  IElementType EQUAL = new RsElementType("=");
  IElementType EXCEL = new RsElementType("!");
  IElementType FALSE = new RsElementType("FALSE");
  IElementType GT = new RsElementType(">");
  IElementType GTE = new RsElementType(">=");
  IElementType IDENTIFIER = new RsElementType("IDENTIFIER");
  IElementType IF = new RsElementType("IF");
  IElementType INTEGER = new RsElementType("INTEGER");
  IElementType LBRACE = new RsElementType("{");
  IElementType LBRACKET = new RsElementType("[");
  IElementType LPAREN = new RsElementType("(");
  IElementType LT = new RsElementType("<");
  IElementType LTE = new RsElementType("<=");
  IElementType MINUS = new RsElementType("-");
  IElementType NULL = new RsElementType("NULL");
  IElementType PERCENT = new RsElementType("%");
  IElementType PLUS = new RsElementType("+");
  IElementType RBRACE = new RsElementType("}");
  IElementType RBRACKET = new RsElementType("]");
  IElementType RETURN = new RsElementType("RETURN");
  IElementType RPAREN = new RsElementType(")");
  IElementType SEMICOLON = new RsElementType(";");
  IElementType SLASH = new RsElementType("/");
  IElementType STAR = new RsElementType("*");
  IElementType STRING_END = new RsElementType("STRING_END");
  IElementType STRING_INTERPOLATION_END = new RsElementType("STRING_INTERPOLATION_END");
  IElementType STRING_INTERPOLATION_START = new RsElementType("STRING_INTERPOLATION_START");
  IElementType STRING_PART = new RsElementType("STRING_PART");
  IElementType STRING_START = new RsElementType("STRING_START");
  IElementType STRING_TAG = new RsElementType("STRING_TAG");
  IElementType SWITCH = new RsElementType("SWITCH");
  IElementType TILDE = new RsElementType("~");
  IElementType TRUE = new RsElementType("TRUE");
  IElementType TYPE_LITERAL = new RsElementType("TYPE_LITERAL");
  IElementType WHILE = new RsElementType("WHILE");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == ARGUMENT_LIST) {
        return new RsArgumentListImpl(node);
      }
      else if (type == ARITHMETIC_EXPRESSION) {
        return new RsArithmeticExpressionImpl(node);
      }
      else if (type == ARITHMETIC_OP) {
        return new RsArithmeticOpImpl(node);
      }
      else if (type == ARITHMETIC_VALUE_EXPRESSION) {
        return new RsArithmeticValueExpressionImpl(node);
      }
      else if (type == ARRAY_ACCESS_EXPRESSION) {
        return new RsArrayAccessExpressionImpl(node);
      }
      else if (type == ARRAY_VARIABLE_DECLARATION_STATEMENT) {
        return new RsArrayVariableDeclarationStatementImpl(node);
      }
      else if (type == ASSIGNMENT_STATEMENT) {
        return new RsAssignmentStatementImpl(node);
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
      else if (type == CONDITION_EXPRESSION) {
        return new RsConditionExpressionImpl(node);
      }
      else if (type == CONDITION_OP) {
        return new RsConditionOpImpl(node);
      }
      else if (type == CONSTANT_EXPRESSION) {
        return new RsConstantExpressionImpl(node);
      }
      else if (type == COORD_LITERAL_EXPRESSION) {
        return new RsCoordLiteralExpressionImpl(node);
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
      else if (type == HOOK_FRAGMENT) {
        return new RsHookFragmentImpl(node);
      }
      else if (type == HOOK_ROOT) {
        return new RsHookRootImpl(node);
      }
      else if (type == HOOK_TRANSMIT_LIST) {
        return new RsHookTransmitListImpl(node);
      }
      else if (type == IF_STATEMENT) {
        return new RsIfStatementImpl(node);
      }
      else if (type == INTEGER_LITERAL_EXPRESSION) {
        return new RsIntegerLiteralExpressionImpl(node);
      }
      else if (type == LOCAL_VARIABLE_DECLARATION_STATEMENT) {
        return new RsLocalVariableDeclarationStatementImpl(node);
      }
      else if (type == LOCAL_VARIABLE_EXPRESSION) {
        return new RsLocalVariableExpressionImpl(node);
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
      else if (type == SCOPED_VARIABLE_EXPRESSION) {
        return new RsScopedVariableExpressionImpl(node);
      }
      else if (type == SCRIPT) {
        return new RsScriptImpl(node);
      }
      else if (type == STATEMENT_LIST) {
        return new RsStatementListImpl(node);
      }
      else if (type == STRING_INTERPOLATION_EXPRESSION) {
        return new RsStringInterpolationExpressionImpl(node);
      }
      else if (type == STRING_LITERAL_CONTENT) {
        return new RsStringLiteralContentImpl(node);
      }
      else if (type == STRING_LITERAL_EXPRESSION) {
        return new RsStringLiteralExpressionImpl(node);
      }
      else if (type == SWITCH_CASE) {
        return new RsSwitchCaseImpl(node);
      }
      else if (type == SWITCH_CASE_DEFAULT_EXPRESSION) {
        return new RsSwitchCaseDefaultExpressionImpl(node);
      }
      else if (type == SWITCH_STATEMENT) {
        return new RsSwitchStatementImpl(node);
      }
      else if (type == TYPE_NAME) {
        return new RsTypeNameImpl(node);
      }
      else if (type == WHILE_STATEMENT) {
        return new RsWhileStatementImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}

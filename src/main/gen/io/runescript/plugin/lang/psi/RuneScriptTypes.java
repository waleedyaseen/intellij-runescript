// This is a generated file. Not intended for manual editing.
package io.runescript.plugin.lang.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import io.runescript.plugin.lang.psi.impl.*;

public interface RuneScriptTypes {

  IElementType ARRAY_VARIABLE_ASSIGNMENT_STATEMENT = new RuneScriptElementType("ARRAY_VARIABLE_ASSIGNMENT_STATEMENT");
  IElementType ARRAY_VARIABLE_DECLARATION_STATEMENT = new RuneScriptElementType("ARRAY_VARIABLE_DECLARATION_STATEMENT");
  IElementType BLOCK_STATEMENT = new RuneScriptElementType("BLOCK_STATEMENT");
  IElementType BOOLEAN_LITERAL_EXPRESSION = new RuneScriptElementType("BOOLEAN_LITERAL_EXPRESSION");
  IElementType COMMAND_EXPRESSION = new RuneScriptElementType("COMMAND_EXPRESSION");
  IElementType DYNAMIC_EXPRESSION = new RuneScriptElementType("DYNAMIC_EXPRESSION");
  IElementType EXPRESSION = new RuneScriptElementType("EXPRESSION");
  IElementType EXPRESSION_STATEMENT = new RuneScriptElementType("EXPRESSION_STATEMENT");
  IElementType GOSUB_EXPRESSION = new RuneScriptElementType("GOSUB_EXPRESSION");
  IElementType IF_STATEMENT = new RuneScriptElementType("IF_STATEMENT");
  IElementType INTEGER_LITERAL_EXPRESSION = new RuneScriptElementType("INTEGER_LITERAL_EXPRESSION");
  IElementType LOCAL_VARIABLE_ASSIGNMENT_STATEMENT = new RuneScriptElementType("LOCAL_VARIABLE_ASSIGNMENT_STATEMENT");
  IElementType LOCAL_VARIABLE_DECLARATION_STATEMENT = new RuneScriptElementType("LOCAL_VARIABLE_DECLARATION_STATEMENT");
  IElementType LOCAL_VARIABLE_EXPRESSION = new RuneScriptElementType("LOCAL_VARIABLE_EXPRESSION");
  IElementType NULL_LITERAL_EXPRESSION = new RuneScriptElementType("NULL_LITERAL_EXPRESSION");
  IElementType PARAMETER = new RuneScriptElementType("PARAMETER");
  IElementType PARAMETER_LIST = new RuneScriptElementType("PARAMETER_LIST");
  IElementType PAR_EXPRESSION = new RuneScriptElementType("PAR_EXPRESSION");
  IElementType RETURN_LIST = new RuneScriptElementType("RETURN_LIST");
  IElementType RETURN_STATEMENT = new RuneScriptElementType("RETURN_STATEMENT");
  IElementType SCRIPT = new RuneScriptElementType("SCRIPT");
  IElementType SCRIPT_HEADER = new RuneScriptElementType("SCRIPT_HEADER");
  IElementType STATEMENT = new RuneScriptElementType("STATEMENT");
  IElementType STATEMENT_LIST = new RuneScriptElementType("STATEMENT_LIST");
  IElementType SWITCH_CASE = new RuneScriptElementType("SWITCH_CASE");
  IElementType SWITCH_STATEMENT = new RuneScriptElementType("SWITCH_STATEMENT");
  IElementType WHILE_STATEMENT = new RuneScriptElementType("WHILE_STATEMENT");

  IElementType CASE = new RuneScriptElementType("CASE");
  IElementType COLON = new RuneScriptElementType("COLON");
  IElementType COMMA = new RuneScriptElementType("COMMA");
  IElementType DEFINE_TYPE = new RuneScriptElementType("DEFINE_TYPE");
  IElementType DOLLAR = new RuneScriptElementType("DOLLAR");
  IElementType EQUAL = new RuneScriptElementType("EQUAL");
  IElementType FALSE = new RuneScriptElementType("FALSE");
  IElementType IDENTIFIER = new RuneScriptElementType("IDENTIFIER");
  IElementType IF = new RuneScriptElementType("IF");
  IElementType INTEGER = new RuneScriptElementType("INTEGER");
  IElementType LBRACE = new RuneScriptElementType("LBRACE");
  IElementType LBRACKET = new RuneScriptElementType("LBRACKET");
  IElementType LPAREN = new RuneScriptElementType("LPAREN");
  IElementType NULL = new RuneScriptElementType("NULL");
  IElementType RBRACE = new RuneScriptElementType("RBRACE");
  IElementType RBRACKET = new RuneScriptElementType("RBRACKET");
  IElementType RETURN = new RuneScriptElementType("RETURN");
  IElementType RPAREN = new RuneScriptElementType("RPAREN");
  IElementType SEMICOLON = new RuneScriptElementType("SEMICOLON");
  IElementType SWITCH = new RuneScriptElementType("SWITCH");
  IElementType TILDE = new RuneScriptElementType("TILDE");
  IElementType TRUE = new RuneScriptElementType("TRUE");
  IElementType TYPE_NAME = new RuneScriptElementType("TYPE_NAME");
  IElementType WHILE = new RuneScriptElementType("WHILE");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == ARRAY_VARIABLE_ASSIGNMENT_STATEMENT) {
        return new RuneScriptArrayVariableAssignmentStatementImpl(node);
      }
      else if (type == ARRAY_VARIABLE_DECLARATION_STATEMENT) {
        return new RuneScriptArrayVariableDeclarationStatementImpl(node);
      }
      else if (type == BLOCK_STATEMENT) {
        return new RuneScriptBlockStatementImpl(node);
      }
      else if (type == BOOLEAN_LITERAL_EXPRESSION) {
        return new RuneScriptBooleanLiteralExpressionImpl(node);
      }
      else if (type == COMMAND_EXPRESSION) {
        return new RuneScriptCommandExpressionImpl(node);
      }
      else if (type == DYNAMIC_EXPRESSION) {
        return new RuneScriptDynamicExpressionImpl(node);
      }
      else if (type == EXPRESSION_STATEMENT) {
        return new RuneScriptExpressionStatementImpl(node);
      }
      else if (type == GOSUB_EXPRESSION) {
        return new RuneScriptGosubExpressionImpl(node);
      }
      else if (type == IF_STATEMENT) {
        return new RuneScriptIfStatementImpl(node);
      }
      else if (type == INTEGER_LITERAL_EXPRESSION) {
        return new RuneScriptIntegerLiteralExpressionImpl(node);
      }
      else if (type == LOCAL_VARIABLE_ASSIGNMENT_STATEMENT) {
        return new RuneScriptLocalVariableAssignmentStatementImpl(node);
      }
      else if (type == LOCAL_VARIABLE_DECLARATION_STATEMENT) {
        return new RuneScriptLocalVariableDeclarationStatementImpl(node);
      }
      else if (type == LOCAL_VARIABLE_EXPRESSION) {
        return new RuneScriptLocalVariableExpressionImpl(node);
      }
      else if (type == NULL_LITERAL_EXPRESSION) {
        return new RuneScriptNullLiteralExpressionImpl(node);
      }
      else if (type == PARAMETER) {
        return new RuneScriptParameterImpl(node);
      }
      else if (type == PARAMETER_LIST) {
        return new RuneScriptParameterListImpl(node);
      }
      else if (type == PAR_EXPRESSION) {
        return new RuneScriptParExpressionImpl(node);
      }
      else if (type == RETURN_LIST) {
        return new RuneScriptReturnListImpl(node);
      }
      else if (type == RETURN_STATEMENT) {
        return new RuneScriptReturnStatementImpl(node);
      }
      else if (type == SCRIPT) {
        return new RuneScriptScriptImpl(node);
      }
      else if (type == SCRIPT_HEADER) {
        return new RuneScriptScriptHeaderImpl(node);
      }
      else if (type == STATEMENT_LIST) {
        return new RuneScriptStatementListImpl(node);
      }
      else if (type == SWITCH_CASE) {
        return new RuneScriptSwitchCaseImpl(node);
      }
      else if (type == SWITCH_STATEMENT) {
        return new RuneScriptSwitchStatementImpl(node);
      }
      else if (type == WHILE_STATEMENT) {
        return new RuneScriptWhileStatementImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}

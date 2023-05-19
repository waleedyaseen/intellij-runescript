// This is a generated file. Not intended for manual editing.
package io.runescript.plugin.lang.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import io.runescript.plugin.lang.psi.impl.*;

public interface RuneScriptTypes {

  IElementType BRACED_BLOCK_STATEMENT = new RuneScriptElementType("BRACED_BLOCK_STATEMENT");
  IElementType DYNAMIC_EXPRESSION = new RuneScriptElementType("DYNAMIC_EXPRESSION");
  IElementType EXPRESSION = new RuneScriptElementType("EXPRESSION");
  IElementType IF_STATEMENT = new RuneScriptElementType("IF_STATEMENT");
  IElementType LOCAL_VARIABLE_EXPRESSION = new RuneScriptElementType("LOCAL_VARIABLE_EXPRESSION");
  IElementType PARAMETER = new RuneScriptElementType("PARAMETER");
  IElementType PARAMETER_LIST = new RuneScriptElementType("PARAMETER_LIST");
  IElementType PAR_EXPRESSION = new RuneScriptElementType("PAR_EXPRESSION");
  IElementType RETURN_LIST = new RuneScriptElementType("RETURN_LIST");
  IElementType SCRIPT = new RuneScriptElementType("SCRIPT");
  IElementType SCRIPT_HEADER = new RuneScriptElementType("SCRIPT_HEADER");
  IElementType STATEMENT = new RuneScriptElementType("STATEMENT");
  IElementType STATEMENT_LIST = new RuneScriptElementType("STATEMENT_LIST");
  IElementType WHILE_STATEMENT = new RuneScriptElementType("WHILE_STATEMENT");

  IElementType COMMA = new RuneScriptElementType("COMMA");
  IElementType DOLLAR = new RuneScriptElementType("DOLLAR");
  IElementType IDENTIFIER = new RuneScriptElementType("IDENTIFIER");
  IElementType IF = new RuneScriptElementType("IF");
  IElementType LBRACE = new RuneScriptElementType("LBRACE");
  IElementType LBRACKET = new RuneScriptElementType("LBRACKET");
  IElementType LPAREN = new RuneScriptElementType("LPAREN");
  IElementType RBRACE = new RuneScriptElementType("RBRACE");
  IElementType RBRACKET = new RuneScriptElementType("RBRACKET");
  IElementType RPAREN = new RuneScriptElementType("RPAREN");
  IElementType TYPE_NAME = new RuneScriptElementType("TYPE_NAME");
  IElementType WHILE = new RuneScriptElementType("WHILE");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == BRACED_BLOCK_STATEMENT) {
        return new RuneScriptBracedBlockStatementImpl(node);
      }
      else if (type == DYNAMIC_EXPRESSION) {
        return new RuneScriptDynamicExpressionImpl(node);
      }
      else if (type == IF_STATEMENT) {
        return new RuneScriptIfStatementImpl(node);
      }
      else if (type == LOCAL_VARIABLE_EXPRESSION) {
        return new RuneScriptLocalVariableExpressionImpl(node);
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
      else if (type == SCRIPT) {
        return new RuneScriptScriptImpl(node);
      }
      else if (type == SCRIPT_HEADER) {
        return new RuneScriptScriptHeaderImpl(node);
      }
      else if (type == STATEMENT_LIST) {
        return new RuneScriptStatementListImpl(node);
      }
      else if (type == WHILE_STATEMENT) {
        return new RuneScriptWhileStatementImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}

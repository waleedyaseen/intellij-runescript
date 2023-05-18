// This is a generated file. Not intended for manual editing.
package io.runescript.plugin.lang.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import io.runescript.plugin.lang.psi.impl.*;

public interface RuneScriptTypes {

  IElementType PARAMETER = new RuneScriptElementType("PARAMETER");
  IElementType PARAMETER_LIST = new RuneScriptElementType("PARAMETER_LIST");
  IElementType RETURN_LIST = new RuneScriptElementType("RETURN_LIST");
  IElementType SCRIPT = new RuneScriptElementType("SCRIPT");
  IElementType SCRIPT_HEADER = new RuneScriptElementType("SCRIPT_HEADER");

  IElementType COMMA = new RuneScriptElementType("COMMA");
  IElementType DOLLAR = new RuneScriptElementType("DOLLAR");
  IElementType IDENTIFIER = new RuneScriptElementType("IDENTIFIER");
  IElementType LBRACE = new RuneScriptElementType("LBRACE");
  IElementType LPAREN = new RuneScriptElementType("LPAREN");
  IElementType RBRACE = new RuneScriptElementType("RBRACE");
  IElementType RPAREN = new RuneScriptElementType("RPAREN");
  IElementType TYPE_NAME = new RuneScriptElementType("TYPE_NAME");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == PARAMETER) {
        return new RuneScriptParameterImpl(node);
      }
      else if (type == PARAMETER_LIST) {
        return new RuneScriptParameterListImpl(node);
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
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}

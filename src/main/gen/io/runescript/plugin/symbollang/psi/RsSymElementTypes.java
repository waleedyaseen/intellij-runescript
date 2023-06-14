// This class is automatically generated. Do not edit.
package io.runescript.plugin.symbollang.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import io.runescript.plugin.symbollang.psi.stub.RsSymStubElementFactory;
import io.runescript.plugin.symbollang.psi.impl.*;

public interface RsSymElementTypes {

  IElementType FIELD = RsSymStubElementFactory.create("FIELD");
  IElementType SYMBOL = RsSymStubElementFactory.create("SYMBOL");

  IElementType NEW_LINE = new RsSymElementType("\\n");
  IElementType STRING = new RsSymElementType("STRING");
  IElementType TAB = new RsSymElementType("\\t");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == FIELD) {
        return new RsSymFieldImpl(node);
      }
      else if (type == SYMBOL) {
        return new RsSymSymbolImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}

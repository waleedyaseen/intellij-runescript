// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RsArrayVariableDeclarationStatement extends RsStatement {

  @NotNull
  List<RsExpression> getExpressionList();

  @NotNull
  PsiElement getDefineType();

  @NotNull
  PsiElement getLparen();

  @NotNull
  PsiElement getRparen();

  @NotNull
  PsiElement getSemicolon();

}

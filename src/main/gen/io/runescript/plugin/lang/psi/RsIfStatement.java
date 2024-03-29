// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RsIfStatement extends RsStatement {

  @Nullable
  RsExpression getExpression();

  @NotNull
  List<RsStatement> getStatementList();

  @Nullable
  PsiElement getElse();

  @NotNull
  PsiElement getIf();

  @NotNull
  PsiElement getLparen();

  @Nullable
  PsiElement getRparen();

  @Nullable
  RsStatement getTrueStatement();

  @Nullable
  RsStatement getFalseStatement();

}

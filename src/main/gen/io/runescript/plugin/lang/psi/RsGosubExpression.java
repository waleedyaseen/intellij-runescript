// This is a generated file. Not intended for manual editing.
package io.runescript.plugin.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RsGosubExpression extends RsExpression {

  @NotNull
  List<RsExpression> getExpressionList();

  @NotNull
  RsNameLiteral getNameLiteral();

  @Nullable
  PsiElement getLparen();

  @Nullable
  PsiElement getRparen();

  @NotNull
  PsiElement getTilde();

}

// This is a generated file. Not intended for manual editing.
package io.runescript.plugin.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RsCompareExpression extends RsExpression {

  @NotNull
  List<RsExpression> getExpressionList();

  @Nullable
  PsiElement getEqual();

  @Nullable
  PsiElement getExcel();

  @Nullable
  PsiElement getGt();

  @Nullable
  PsiElement getGte();

  @Nullable
  PsiElement getLt();

  @Nullable
  PsiElement getLte();

}

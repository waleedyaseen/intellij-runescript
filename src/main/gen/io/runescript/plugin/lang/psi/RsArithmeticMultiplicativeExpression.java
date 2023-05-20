// This is a generated file. Not intended for manual editing.
package io.runescript.plugin.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RsArithmeticMultiplicativeExpression extends RsExpression {

  @NotNull
  List<RsExpression> getExpressionList();

  @Nullable
  PsiElement getPercent();

  @Nullable
  PsiElement getSlash();

  @Nullable
  PsiElement getStar();

}

// This is a generated file. Not intended for manual editing.
package io.runescript.plugin.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RsReturnStatement extends RsStatement {

  @NotNull
  List<RsExpression> getExpressionList();

  @Nullable
  PsiElement getLparen();

  @NotNull
  PsiElement getReturn();

  @Nullable
  PsiElement getRparen();

  @NotNull
  PsiElement getSemicolon();

}

// This is a generated file. Not intended for manual editing.
package io.runescript.plugin.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RsCalcExpression extends RsExpression {

  @NotNull
  RsExpression getExpression();

  @NotNull
  PsiElement getCalc();

  @NotNull
  PsiElement getLparen();

  @NotNull
  PsiElement getRparen();

}

// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RsSwitchStatement extends RsStatement {

  @Nullable
  RsExpression getExpression();

  @NotNull
  List<RsSwitchCase> getSwitchCaseList();

  @Nullable
  PsiElement getLbrace();

  @NotNull
  PsiElement getLparen();

  @Nullable
  PsiElement getRbrace();

  @Nullable
  PsiElement getRparen();

  @NotNull
  PsiElement getSwitch();

}

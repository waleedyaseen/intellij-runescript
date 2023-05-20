// This is a generated file. Not intended for manual editing.
package io.runescript.plugin.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RsSwitchStatement extends RsStatement {

  @NotNull
  RsExpression getExpression();

  @NotNull
  List<RsSwitchCase> getSwitchCaseList();

  @NotNull
  PsiElement getLbrace();

  @NotNull
  PsiElement getLparen();

  @NotNull
  PsiElement getRbrace();

  @NotNull
  PsiElement getRparen();

  @NotNull
  PsiElement getSwitch();

}

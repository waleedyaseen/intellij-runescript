// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RsLocalVariableExpression extends RsExpression {

  @NotNull
  RsNameLiteral getNameLiteral();

  @NotNull
  PsiElement getDollar();

  @NotNull
  String getVariableName();

  @NotNull
  PsiElement setVariableName(@NotNull String newName);

  @NotNull
  PsiElement getNameIdentifier();

}

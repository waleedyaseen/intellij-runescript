// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import io.runescript.plugin.lang.stubs.RsLocalVariableExpressionStub;

public interface RsLocalVariableExpression extends RsExpression, StubBasedPsiElement<RsLocalVariableExpressionStub> {

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

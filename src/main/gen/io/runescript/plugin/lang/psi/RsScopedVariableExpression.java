// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import io.runescript.plugin.lang.stubs.RsScopedVariableExpressionStub;

public interface RsScopedVariableExpression extends RsExpression, RsNamedElement, StubBasedPsiElement<RsScopedVariableExpressionStub> {

  @NotNull
  RsNameLiteral getNameLiteral();

  @NotNull
  PsiElement getPercent();

}

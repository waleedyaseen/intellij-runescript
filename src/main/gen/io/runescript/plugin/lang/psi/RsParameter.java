// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import io.runescript.plugin.lang.stubs.RsParameterStub;

public interface RsParameter extends PsiElement, StubBasedPsiElement<RsParameterStub> {

  @NotNull
  RsLocalVariableExpression getLocalVariableExpression();

  @Nullable
  RsTypeName getTypeName();

  @Nullable
  PsiElement getArrayTypeLiteral();

}

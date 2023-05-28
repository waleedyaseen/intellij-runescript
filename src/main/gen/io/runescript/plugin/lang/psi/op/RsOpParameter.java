// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi.op;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import io.runescript.plugin.lang.stubs.RsOpParameterStub;

public interface RsOpParameter extends PsiElement, StubBasedPsiElement<RsOpParameterStub> {

  @NotNull
  RsOpNameLiteral getNameLiteral();

  @NotNull
  RsOpTypeName getTypeName();

  @NotNull
  PsiElement getDollar();

}

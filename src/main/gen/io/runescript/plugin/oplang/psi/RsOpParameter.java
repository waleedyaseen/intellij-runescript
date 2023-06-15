// This class is automatically generated. Do not edit.
package io.runescript.plugin.oplang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import io.runescript.plugin.oplang.psi.stub.RsOpParameterStub;

public interface RsOpParameter extends PsiElement, StubBasedPsiElement<RsOpParameterStub> {

  @NotNull
  RsOpNameLiteral getNameLiteral();

  @NotNull
  RsOpTypeName getTypeName();

  @NotNull
  PsiElement getDollar();

}

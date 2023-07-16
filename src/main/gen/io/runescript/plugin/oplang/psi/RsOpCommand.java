// This class is automatically generated. Do not edit.
package io.runescript.plugin.oplang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import io.runescript.plugin.oplang.psi.stub.RsOpCommandStub;

public interface RsOpCommand extends PsiElement, StubBasedPsiElement<RsOpCommandStub> {

  @NotNull
  RsOpAttributeList getAttributeList();

  @NotNull
  RsOpCommandHeader getCommandHeader();

  @NotNull
  RsOpParameterList getParameterList();

  @NotNull
  RsOpReturnList getReturnList();

}
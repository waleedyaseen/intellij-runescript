// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi.op;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import io.runescript.plugin.lang.stubs.RsOpParameterListStub;

public interface RsOpParameterList extends PsiElement, StubBasedPsiElement<RsOpParameterListStub> {

  @NotNull
  List<RsOpParameter> getParameterList();

  @NotNull
  PsiElement getLparen();

  @NotNull
  PsiElement getRparen();

}

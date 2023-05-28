// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import io.runescript.plugin.lang.stubs.RsParameterListStub;

public interface RsParameterList extends PsiElement, StubBasedPsiElement<RsParameterListStub> {

  @NotNull
  List<RsParameter> getParameterList();

  @NotNull
  PsiElement getLparen();

  @NotNull
  PsiElement getRparen();

}

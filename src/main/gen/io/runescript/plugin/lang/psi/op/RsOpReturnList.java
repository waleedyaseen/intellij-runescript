// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi.op;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import io.runescript.plugin.lang.stubs.RsOpReturnListStub;

public interface RsOpReturnList extends PsiElement, StubBasedPsiElement<RsOpReturnListStub> {

  @NotNull
  List<RsOpTypeName> getTypeNameList();

  @NotNull
  PsiElement getLparen();

  @NotNull
  PsiElement getRparen();

}

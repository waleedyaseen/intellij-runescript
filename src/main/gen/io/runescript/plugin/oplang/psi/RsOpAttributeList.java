// This class is automatically generated. Do not edit.
package io.runescript.plugin.oplang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import io.runescript.plugin.oplang.psi.stub.RsOpAttributeListStub;

public interface RsOpAttributeList extends PsiElement, StubBasedPsiElement<RsOpAttributeListStub> {

  @NotNull
  List<RsOpAttribute> getAttributeList();

}

// This class is automatically generated. Do not edit.
package io.runescript.plugin.symbollang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import io.runescript.plugin.lang.psi.RsNamedElement;
import com.intellij.psi.StubBasedPsiElement;
import io.runescript.plugin.symbollang.psi.stub.RsSymSymbolStub;

public interface RsSymSymbol extends RsNamedElement, StubBasedPsiElement<RsSymSymbolStub> {

  @NotNull
  List<RsSymField> getFieldList();

  @NotNull
  PsiElement getNewLine();

}

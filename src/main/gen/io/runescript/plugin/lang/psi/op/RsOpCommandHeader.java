// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi.op;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import io.runescript.plugin.lang.stubs.RsOpCommandHeaderStub;

public interface RsOpCommandHeader extends PsiElement, StubBasedPsiElement<RsOpCommandHeaderStub> {

  @NotNull
  List<RsOpNameLiteral> getNameLiteralList();

  @NotNull
  PsiElement getComma();

  @NotNull
  PsiElement getLbracket();

  @NotNull
  PsiElement getRbracket();

}

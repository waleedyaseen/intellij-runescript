// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.StubBasedPsiElement;
import io.runescript.plugin.lang.stubs.RsScriptNameStub;

public interface RsScriptName extends NavigatablePsiElement, StubBasedPsiElement<RsScriptNameStub> {

  @NotNull
  List<RsNameLiteral> getNameLiteralList();

  @NotNull
  PsiElement getComma();

  @NotNull
  PsiElement getLbracket();

  @NotNull
  PsiElement getRbracket();

  @NotNull
  RsNameLiteral getTriggerExpression();

  @Nullable
  RsNameLiteral getNameExpression();

}

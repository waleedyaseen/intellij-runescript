// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.NavigatablePsiElement;
import io.runescript.plugin.lang.psi.type.inference.RsInferenceDataHolder;
import com.intellij.psi.StubBasedPsiElement;
import io.runescript.plugin.lang.stubs.RsScriptStub;

public interface RsScript extends NavigatablePsiElement, RsControlFlowHolder, RsNamedElement, RsInferenceDataHolder, StubBasedPsiElement<RsScriptStub> {

  @NotNull
  List<RsNameLiteral> getNameLiteralList();

  @Nullable
  RsParameterList getParameterList();

  @Nullable
  RsReturnList getReturnList();

  @NotNull
  RsStatementList getStatementList();

  @NotNull
  PsiElement getComma();

  @NotNull
  PsiElement getLbracket();

  @NotNull
  PsiElement getRbracket();

}

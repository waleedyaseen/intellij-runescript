// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.StubBasedPsiElement;
import io.runescript.plugin.lang.stubs.RsScriptStub;

public interface RsScript extends NavigatablePsiElement, RsControlFlowHolder, StubBasedPsiElement<RsScriptStub> {

  @NotNull
  RsScriptHeader getScriptHeader();

  @NotNull
  RsStatementList getStatementList();

}

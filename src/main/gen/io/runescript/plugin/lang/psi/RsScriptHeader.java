// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import io.runescript.plugin.lang.stubs.RsScriptHeaderStub;

public interface RsScriptHeader extends PsiElement, StubBasedPsiElement<RsScriptHeaderStub> {

  @Nullable
  RsParameterList getParameterList();

  @Nullable
  RsReturnList getReturnList();

  @NotNull
  RsScriptName getScriptName();

}

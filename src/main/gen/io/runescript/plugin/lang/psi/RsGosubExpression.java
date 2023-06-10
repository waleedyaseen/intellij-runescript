// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RsGosubExpression extends RsExpression, RsNamedElement {

  @Nullable
  RsArgumentList getArgumentList();

  @NotNull
  RsNameLiteral getNameLiteral();

  @NotNull
  PsiElement getTilde();

}

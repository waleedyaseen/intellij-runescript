// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import io.runescript.plugin.lang.psi.named.RsNamedElement;

public interface RsLocalVariableExpression extends RsExpression, RsNamedElement {

  @NotNull
  RsNameLiteral getNameLiteral();

  @NotNull
  PsiElement getDollar();

  @NotNull
  String getName();

  @NotNull
  PsiElement setName(@NotNull String newName);

  @NotNull
  PsiElement getNameIdentifier();

}

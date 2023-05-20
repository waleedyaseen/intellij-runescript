// This is a generated file. Not intended for manual editing.
package io.runescript.plugin.lang.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import io.runescript.plugin.lang.psi.named.RsNamedElement;

public interface RsLocalVariableExpression extends RsExpression, RsNamedElement {

  @NotNull
  RsNameLiteral getNameLiteral();

  @NotNull
  String getName();

  @NotNull
  PsiElement setName(@NotNull String newName);

  @NotNull
  PsiElement getNameIdentifier();

}

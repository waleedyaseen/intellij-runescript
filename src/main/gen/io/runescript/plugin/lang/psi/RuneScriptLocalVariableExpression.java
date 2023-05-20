// This is a generated file. Not intended for manual editing.
package io.runescript.plugin.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import io.runescript.plugin.lang.psi.named.RuneScriptNamedElement;

public interface RuneScriptLocalVariableExpression extends RuneScriptExpression, RuneScriptNamedElement {

  @NotNull
  RuneScriptNameLiteral getNameLiteral();

  @NotNull
  String getName();

  @NotNull
  PsiElement setName(@NotNull String newName);

  @NotNull
  PsiElement getNameIdentifier();

}
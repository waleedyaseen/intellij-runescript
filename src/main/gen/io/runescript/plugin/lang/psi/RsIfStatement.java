// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RsIfStatement extends RsStatement {

  @NotNull
  RsExpression getExpression();

  @NotNull
  RsStatement getStatement();

  @NotNull
  PsiElement getIf();

  @NotNull
  PsiElement getLparen();

  @NotNull
  PsiElement getRparen();

}

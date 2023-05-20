// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RsSwitchCase extends PsiElement {

  @NotNull
  List<RsExpression> getExpressionList();

  @NotNull
  RsStatementList getStatementList();

  @NotNull
  PsiElement getCase();

  @NotNull
  PsiElement getColon();

}

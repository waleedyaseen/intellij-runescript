// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RsArrayVariableAssignmentStatement extends RsStatement {

  @NotNull
  List<RsExpression> getExpressionList();

  @NotNull
  PsiElement getEqual();

  @NotNull
  PsiElement getSemicolon();

}

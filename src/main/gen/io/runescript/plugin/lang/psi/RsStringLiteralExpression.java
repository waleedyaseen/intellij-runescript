// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RsStringLiteralExpression extends RsExpression {

  @NotNull
  List<RsExpression> getExpressionList();

  @NotNull
  PsiElement getStringEnd();

  @NotNull
  PsiElement getStringStart();

}
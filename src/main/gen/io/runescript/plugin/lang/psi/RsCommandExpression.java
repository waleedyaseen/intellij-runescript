// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.NavigatablePsiElement;

public interface RsCommandExpression extends RsExpression, NavigatablePsiElement, RsCallExpression {

  @NotNull
  List<RsArgumentList> getArgumentListList();

  @NotNull
  RsNameLiteral getNameLiteral();

  @Nullable
  PsiElement getStar();

  @NotNull
  RsArgumentList getArgs();

  @Nullable
  RsArgumentList getArgs2();

  boolean isStar();

  @NotNull String getNameString();

}

// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RsConditionExpression extends RsExpression, RsBinaryExpression {

  @NotNull
  RsConditionOp getConditionOp();

  @NotNull
  List<RsExpression> getExpressionList();

  @NotNull
  RsExpression getLeft();

  @Nullable
  RsExpression getRight();

}

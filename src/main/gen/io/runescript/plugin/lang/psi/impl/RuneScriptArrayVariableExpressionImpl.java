// This is a generated file. Not intended for manual editing.
package io.runescript.plugin.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.runescript.plugin.lang.psi.RuneScriptTypes.*;
import io.runescript.plugin.lang.psi.*;

public class RuneScriptArrayVariableExpressionImpl extends RuneScriptExpressionImpl implements RuneScriptArrayVariableExpression {

  public RuneScriptArrayVariableExpressionImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull RuneScriptVisitor visitor) {
    visitor.visitArrayVariableExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RuneScriptVisitor) accept((RuneScriptVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public RuneScriptExpression getExpression() {
    return findNotNullChildByClass(RuneScriptExpression.class);
  }

}

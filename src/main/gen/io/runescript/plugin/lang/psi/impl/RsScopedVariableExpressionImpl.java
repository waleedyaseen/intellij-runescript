// This is a generated file. Not intended for manual editing.
package io.runescript.plugin.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.runescript.plugin.lang.psi.RsTypes.*;
import io.runescript.plugin.lang.psi.*;

public class RsScopedVariableExpressionImpl extends RsExpressionImpl implements RsScopedVariableExpression {

  public RsScopedVariableExpressionImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull RsVisitor visitor) {
    visitor.visitScopedVariableExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RsVisitor) accept((RsVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public RsNameLiteral getNameLiteral() {
    return findNotNullChildByClass(RsNameLiteral.class);
  }

  @Override
  @NotNull
  public PsiElement getMod() {
    return findNotNullChildByType(MOD);
  }

}

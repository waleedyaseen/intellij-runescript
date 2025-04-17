// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.runescript.plugin.lang.psi.RsElementTypes.*;
import io.runescript.plugin.lang.psi.*;

public class RsPostfixExpressionImpl extends RsExpressionImpl implements RsPostfixExpression {

  public RsPostfixExpressionImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull RsVisitor visitor) {
    visitor.visitPostfixExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RsVisitor) accept((RsVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public RsExpression getExpression() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, RsExpression.class));
  }

  @Override
  @NotNull
  public RsUnaryOp getUnaryOp() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, RsUnaryOp.class));
  }

}

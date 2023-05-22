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

public class RsStringInterpolationExpressionImpl extends RsExpressionImpl implements RsStringInterpolationExpression {

  public RsStringInterpolationExpressionImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull RsVisitor visitor) {
    visitor.visitStringInterpolationExpression(this);
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
  public PsiElement getStringInterpolationEnd() {
    return notNullChild(findChildByType(STRING_INTERPOLATION_END));
  }

  @Override
  @NotNull
  public PsiElement getStringInterpolationStart() {
    return notNullChild(findChildByType(STRING_INTERPOLATION_START));
  }

}

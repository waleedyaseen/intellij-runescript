// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.runescript.plugin.lang.psi.RsElementTypes.*;
import io.runescript.plugin.lang.psi.mixin.RsStringLiteralExpressionMixin;
import io.runescript.plugin.lang.psi.*;

public class RsStringLiteralExpressionImpl extends  RsStringLiteralExpressionMixin implements RsStringLiteralExpression {

  public RsStringLiteralExpressionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RsVisitor visitor) {
    visitor.visitStringLiteralExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RsVisitor) accept((RsVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public RsStringLiteralContent getStringLiteralContent() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, RsStringLiteralContent.class));
  }

  @Override
  @NotNull
  public PsiElement getStringEnd() {
    return notNullChild(findChildByType(STRING_END));
  }

  @Override
  @NotNull
  public PsiElement getStringStart() {
    return notNullChild(findChildByType(STRING_START));
  }

}

// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.runescript.plugin.lang.psi.RsElementTypes.*;
import io.runescript.plugin.lang.psi.mixin.RsConstantExpressionMixin;
import io.runescript.plugin.lang.psi.*;

public class RsConstantExpressionImpl extends RsConstantExpressionMixin implements RsConstantExpression {

  public RsConstantExpressionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RsVisitor visitor) {
    visitor.visitConstantExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RsVisitor) accept((RsVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public RsNameLiteral getNameLiteral() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, RsNameLiteral.class));
  }

  @Override
  @NotNull
  public PsiElement getCaret() {
    return notNullChild(findChildByType(CARET));
  }

}

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

public class RsConditionExpressionImpl extends RsExpressionImpl implements RsConditionExpression {

  public RsConditionExpressionImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull RsVisitor visitor) {
    visitor.visitConditionExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RsVisitor) accept((RsVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public RsConditionOp getConditionOp() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, RsConditionOp.class));
  }

  @Override
  @NotNull
  public List<RsExpression> getExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RsExpression.class);
  }

  @Override
  @NotNull
  public RsExpression getLeft() {
    List<RsExpression> p1 = getExpressionList();
    return p1.get(0);
  }

  @Override
  @Nullable
  public RsExpression getRight() {
    List<RsExpression> p1 = getExpressionList();
    return p1.size() < 2 ? null : p1.get(1);
  }

}

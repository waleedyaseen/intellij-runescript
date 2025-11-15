// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.runescript.plugin.lang.psi.RsElementTypes.*;
import io.runescript.plugin.lang.psi.mixin.RsCommandExpressionMixin;
import io.runescript.plugin.lang.psi.*;

public class RsCommandExpressionImpl extends  RsCommandExpressionMixin implements RsCommandExpression {

  public RsCommandExpressionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RsVisitor visitor) {
    visitor.visitCommandExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RsVisitor) accept((RsVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<RsArgumentList> getArgumentListList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RsArgumentList.class);
  }

  @Override
  @NotNull
  public RsNameLiteral getNameLiteral() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, RsNameLiteral.class));
  }

  @Override
  @Nullable
  public PsiElement getStar() {
    return findChildByType(STAR);
  }

  @Override
  @NotNull
  public RsArgumentList getArgs() {
    List<RsArgumentList> p1 = getArgumentListList();
    return p1.get(0);
  }

  @Override
  @Nullable
  public RsArgumentList getArgs2() {
    List<RsArgumentList> p1 = getArgumentListList();
    return p1.size() < 2 ? null : p1.get(1);
  }

  @Override
  public boolean isStar() {
    return RsPsiImplUtil.isStar(this);
  }

  @Override
  public @NotNull String getNameString() {
    return RsPsiImplUtil.getNameString(this);
  }

}

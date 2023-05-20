// This is a generated file. Not intended for manual editing.
package io.runescript.plugin.lang.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import io.runescript.plugin.lang.psi.impl.naned.RsNamedElementImpl;
import io.runescript.plugin.lang.psi.*;

public class RsLocalVariableExpressionImpl extends RsNamedElementImpl implements RsLocalVariableExpression {

  public RsLocalVariableExpressionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RsVisitor visitor) {
    visitor.visitLocalVariableExpression(this);
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
  public String getName() {
    return RsPsiImplUtil.getName(this);
  }

  @Override
  @NotNull
  public PsiElement setName(@NotNull String newName) {
    return RsPsiImplUtil.setName(this, newName);
  }

  @Override
  @NotNull
  public PsiElement getNameIdentifier() {
    return RsPsiImplUtil.getNameIdentifier(this);
  }

}

// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.runescript.plugin.lang.psi.RsElementTypes.*;
import io.runescript.plugin.lang.psi.mixin.RsLocalVariableExpressionMixin;
import io.runescript.plugin.lang.psi.*;
import io.runescript.plugin.lang.stubs.RsLocalVariableExpressionStub;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;

public class RsLocalVariableExpressionImpl extends RsLocalVariableExpressionMixin implements RsLocalVariableExpression {

  public RsLocalVariableExpressionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public RsLocalVariableExpressionImpl(@NotNull RsLocalVariableExpressionStub stub, @NotNull IStubElementType<?, ?> type) {
    super(stub, type);
  }

  public RsLocalVariableExpressionImpl(@Nullable RsLocalVariableExpressionStub stub, @Nullable IElementType type, @Nullable ASTNode node) {
    super(stub, type, node);
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
    return notNullChild(PsiTreeUtil.getStubChildOfType(this, RsNameLiteral.class));
  }

  @Override
  @NotNull
  public PsiElement getDollar() {
    return notNullChild(findChildByType(DOLLAR));
  }

  @Override
  @NotNull
  public String getVariableName() {
    return RsPsiImplUtil.getVariableName(this);
  }

  @Override
  @NotNull
  public PsiElement setVariableName(@NotNull String newName) {
    return RsPsiImplUtil.setVariableName(this, newName);
  }

  @Override
  @NotNull
  public PsiElement getNameIdentifier() {
    return RsPsiImplUtil.getNameIdentifier(this);
  }

}

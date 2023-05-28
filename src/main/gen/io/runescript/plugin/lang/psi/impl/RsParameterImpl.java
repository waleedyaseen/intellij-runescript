// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.runescript.plugin.lang.psi.RsElementTypes.*;
import com.intellij.extapi.psi.StubBasedPsiElementBase;
import io.runescript.plugin.lang.stubs.RsParameterStub;
import io.runescript.plugin.lang.psi.*;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;

public class RsParameterImpl extends StubBasedPsiElementBase<RsParameterStub> implements RsParameter {

  public RsParameterImpl(@NotNull RsParameterStub stub, @NotNull IStubElementType<?, ?> type) {
    super(stub, type);
  }

  public RsParameterImpl(@NotNull ASTNode node) {
    super(node);
  }

  public RsParameterImpl(RsParameterStub stub, IElementType type, ASTNode node) {
    super(stub, type, node);
  }

  public void accept(@NotNull RsVisitor visitor) {
    visitor.visitParameter(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RsVisitor) accept((RsVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public RsLocalVariableExpression getLocalVariableExpression() {
    return notNullChild(PsiTreeUtil.getStubChildOfType(this, RsLocalVariableExpression.class));
  }

  @Override
  @Nullable
  public RsTypeName getTypeName() {
    return PsiTreeUtil.getStubChildOfType(this, RsTypeName.class);
  }

  @Override
  @Nullable
  public PsiElement getArrayTypeLiteral() {
    return findChildByType(ARRAY_TYPE_LITERAL);
  }

}

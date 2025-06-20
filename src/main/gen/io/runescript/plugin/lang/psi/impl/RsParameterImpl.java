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
import org.jetbrains.annotations.ApiStatus.Experimental;
import com.intellij.psi.tree.IElementType;

public class RsParameterImpl extends StubBasedPsiElementBase<RsParameterStub> implements RsParameter {

  public RsParameterImpl(@NotNull RsParameterStub stub, @NotNull IStubElementType<?, ?> type) {
    super(stub, type);
  }

  public RsParameterImpl(@NotNull RsParameterStub stub, @NotNull IElementType type) {
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
  @Nullable
  public RsLocalVariableExpression getLocalVariableExpression() {
    return PsiTreeUtil.getStubChildOfType(this, RsLocalVariableExpression.class);
  }

  @Override
  @NotNull
  public RsTypeName getTypeName() {
    return notNullChild(PsiTreeUtil.getStubChildOfType(this, RsTypeName.class));
  }

}

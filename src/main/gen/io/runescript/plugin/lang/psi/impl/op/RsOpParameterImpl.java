// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi.impl.op;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.runescript.plugin.lang.psi.op.RsOpElementTypes.*;
import com.intellij.extapi.psi.StubBasedPsiElementBase;
import io.runescript.plugin.lang.stubs.RsOpParameterStub;
import io.runescript.plugin.lang.psi.op.*;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;

public class RsOpParameterImpl extends StubBasedPsiElementBase<RsOpParameterStub> implements RsOpParameter {

  public RsOpParameterImpl(@NotNull RsOpParameterStub stub, @NotNull IStubElementType<?, ?> type) {
    super(stub, type);
  }

  public RsOpParameterImpl(@NotNull ASTNode node) {
    super(node);
  }

  public RsOpParameterImpl(RsOpParameterStub stub, IElementType type, ASTNode node) {
    super(stub, type, node);
  }

  public void accept(@NotNull RsOpVisitor visitor) {
    visitor.visitParameter(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RsOpVisitor) accept((RsOpVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public RsOpNameLiteral getNameLiteral() {
    return notNullChild(PsiTreeUtil.getStubChildOfType(this, RsOpNameLiteral.class));
  }

  @Override
  @NotNull
  public RsOpTypeName getTypeName() {
    return notNullChild(PsiTreeUtil.getStubChildOfType(this, RsOpTypeName.class));
  }

  @Override
  @NotNull
  public PsiElement getDollar() {
    return notNullChild(findChildByType(DOLLAR));
  }

}

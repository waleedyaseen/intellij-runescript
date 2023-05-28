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
import io.runescript.plugin.lang.stubs.RsOpCommandStub;
import io.runescript.plugin.lang.psi.op.*;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;

public class RsOpCommandImpl extends StubBasedPsiElementBase<RsOpCommandStub> implements RsOpCommand {

  public RsOpCommandImpl(@NotNull RsOpCommandStub stub, @NotNull IStubElementType<?, ?> type) {
    super(stub, type);
  }

  public RsOpCommandImpl(@NotNull ASTNode node) {
    super(node);
  }

  public RsOpCommandImpl(RsOpCommandStub stub, IElementType type, ASTNode node) {
    super(stub, type, node);
  }

  public void accept(@NotNull RsOpVisitor visitor) {
    visitor.visitCommand(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RsOpVisitor) accept((RsOpVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public RsOpAttributeList getAttributeList() {
    return notNullChild(PsiTreeUtil.getStubChildOfType(this, RsOpAttributeList.class));
  }

  @Override
  @NotNull
  public RsOpCommandHeader getCommandHeader() {
    return notNullChild(PsiTreeUtil.getStubChildOfType(this, RsOpCommandHeader.class));
  }

  @Override
  @NotNull
  public RsOpParameterList getParameterList() {
    return notNullChild(PsiTreeUtil.getStubChildOfType(this, RsOpParameterList.class));
  }

  @Override
  @NotNull
  public RsOpReturnList getReturnList() {
    return notNullChild(PsiTreeUtil.getStubChildOfType(this, RsOpReturnList.class));
  }

}

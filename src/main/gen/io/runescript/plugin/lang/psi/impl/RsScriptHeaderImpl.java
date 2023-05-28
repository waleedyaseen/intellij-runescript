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
import io.runescript.plugin.lang.stubs.RsScriptHeaderStub;
import io.runescript.plugin.lang.psi.*;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;

public class RsScriptHeaderImpl extends StubBasedPsiElementBase<RsScriptHeaderStub> implements RsScriptHeader {

  public RsScriptHeaderImpl(@NotNull RsScriptHeaderStub stub, @NotNull IStubElementType<?, ?> type) {
    super(stub, type);
  }

  public RsScriptHeaderImpl(@NotNull ASTNode node) {
    super(node);
  }

  public RsScriptHeaderImpl(RsScriptHeaderStub stub, IElementType type, ASTNode node) {
    super(stub, type, node);
  }

  public void accept(@NotNull RsVisitor visitor) {
    visitor.visitScriptHeader(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RsVisitor) accept((RsVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public RsParameterList getParameterList() {
    return PsiTreeUtil.getStubChildOfType(this, RsParameterList.class);
  }

  @Override
  @Nullable
  public RsReturnList getReturnList() {
    return PsiTreeUtil.getStubChildOfType(this, RsReturnList.class);
  }

  @Override
  @NotNull
  public RsScriptName getScriptName() {
    return notNullChild(PsiTreeUtil.getStubChildOfType(this, RsScriptName.class));
  }

}

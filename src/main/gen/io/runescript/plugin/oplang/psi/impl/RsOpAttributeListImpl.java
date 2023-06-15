// This class is automatically generated. Do not edit.
package io.runescript.plugin.oplang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.runescript.plugin.oplang.psi.RsOpElementTypes.*;
import com.intellij.extapi.psi.StubBasedPsiElementBase;
import io.runescript.plugin.oplang.psi.stub.RsOpAttributeListStub;
import io.runescript.plugin.oplang.psi.*;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;

public class RsOpAttributeListImpl extends StubBasedPsiElementBase<RsOpAttributeListStub> implements RsOpAttributeList {

  public RsOpAttributeListImpl(@NotNull RsOpAttributeListStub stub, @NotNull IStubElementType<?, ?> type) {
    super(stub, type);
  }

  public RsOpAttributeListImpl(@NotNull ASTNode node) {
    super(node);
  }

  public RsOpAttributeListImpl(RsOpAttributeListStub stub, IElementType type, ASTNode node) {
    super(stub, type, node);
  }

  public void accept(@NotNull RsOpVisitor visitor) {
    visitor.visitAttributeList(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RsOpVisitor) accept((RsOpVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<RsOpAttribute> getAttributeList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RsOpAttribute.class);
  }

}

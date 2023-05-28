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
import io.runescript.plugin.lang.stubs.RsOpReturnListStub;
import io.runescript.plugin.lang.psi.op.*;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;

public class RsOpReturnListImpl extends StubBasedPsiElementBase<RsOpReturnListStub> implements RsOpReturnList {

  public RsOpReturnListImpl(@NotNull RsOpReturnListStub stub, @NotNull IStubElementType<?, ?> type) {
    super(stub, type);
  }

  public RsOpReturnListImpl(@NotNull ASTNode node) {
    super(node);
  }

  public RsOpReturnListImpl(RsOpReturnListStub stub, IElementType type, ASTNode node) {
    super(stub, type, node);
  }

  public void accept(@NotNull RsOpVisitor visitor) {
    visitor.visitReturnList(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RsOpVisitor) accept((RsOpVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<RsOpTypeName> getTypeNameList() {
    return PsiTreeUtil.getStubChildrenOfTypeAsList(this, RsOpTypeName.class);
  }

  @Override
  @NotNull
  public PsiElement getLparen() {
    return notNullChild(findChildByType(LPAREN));
  }

  @Override
  @NotNull
  public PsiElement getRparen() {
    return notNullChild(findChildByType(RPAREN));
  }

}

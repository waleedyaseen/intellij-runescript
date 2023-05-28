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
import io.runescript.plugin.lang.stubs.RsOpCommandHeaderStub;
import io.runescript.plugin.lang.psi.op.*;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;

public class RsOpCommandHeaderImpl extends StubBasedPsiElementBase<RsOpCommandHeaderStub> implements RsOpCommandHeader {

  public RsOpCommandHeaderImpl(@NotNull RsOpCommandHeaderStub stub, @NotNull IStubElementType<?, ?> type) {
    super(stub, type);
  }

  public RsOpCommandHeaderImpl(@NotNull ASTNode node) {
    super(node);
  }

  public RsOpCommandHeaderImpl(RsOpCommandHeaderStub stub, IElementType type, ASTNode node) {
    super(stub, type, node);
  }

  public void accept(@NotNull RsOpVisitor visitor) {
    visitor.visitCommandHeader(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RsOpVisitor) accept((RsOpVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<RsOpNameLiteral> getNameLiteralList() {
    return PsiTreeUtil.getStubChildrenOfTypeAsList(this, RsOpNameLiteral.class);
  }

  @Override
  @NotNull
  public PsiElement getComma() {
    return notNullChild(findChildByType(COMMA));
  }

  @Override
  @NotNull
  public PsiElement getLbracket() {
    return notNullChild(findChildByType(LBRACKET));
  }

  @Override
  @NotNull
  public PsiElement getRbracket() {
    return notNullChild(findChildByType(RBRACKET));
  }

}

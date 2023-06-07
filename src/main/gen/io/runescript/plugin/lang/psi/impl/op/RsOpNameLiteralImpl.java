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
import io.runescript.plugin.lang.stubs.RsOpNameLiteralStub;
import io.runescript.plugin.lang.psi.op.*;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;

public class RsOpNameLiteralImpl extends StubBasedPsiElementBase<RsOpNameLiteralStub> implements RsOpNameLiteral {

  public RsOpNameLiteralImpl(@NotNull RsOpNameLiteralStub stub, @NotNull IStubElementType<?, ?> type) {
    super(stub, type);
  }

  public RsOpNameLiteralImpl(@NotNull ASTNode node) {
    super(node);
  }

  public RsOpNameLiteralImpl(RsOpNameLiteralStub stub, IElementType type, ASTNode node) {
    super(stub, type, node);
  }

  public void accept(@NotNull RsOpVisitor visitor) {
    visitor.visitNameLiteral(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RsOpVisitor) accept((RsOpVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PsiElement getIdentifier() {
    return findChildByType(IDENTIFIER);
  }

  @Override
  @Nullable
  public PsiElement getTypeLiteral() {
    return findChildByType(TYPE_LITERAL);
  }

}

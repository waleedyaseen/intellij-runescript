// This class is automatically generated. Do not edit.
package io.runescript.plugin.symbollang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.runescript.plugin.symbollang.psi.RsSymElementTypes.*;
import io.runescript.plugin.symbollang.psi.mixin.RsSymSymbolMixin;
import io.runescript.plugin.symbollang.psi.*;
import io.runescript.plugin.symbollang.psi.stub.RsSymSymbolStub;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;

public class RsSymSymbolImpl extends RsSymSymbolMixin implements RsSymSymbol {

  public RsSymSymbolImpl(@NotNull ASTNode node) {
    super(node);
  }

  public RsSymSymbolImpl(@NotNull RsSymSymbolStub stub, @NotNull IStubElementType<?, ?> type) {
    super(stub, type);
  }

  public RsSymSymbolImpl(@Nullable RsSymSymbolStub stub, @Nullable IElementType type, @Nullable ASTNode node) {
    super(stub, type, node);
  }

  public void accept(@NotNull RsSymVisitor visitor) {
    visitor.visitSymbol(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RsSymVisitor) accept((RsSymVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<RsSymField> getFieldList() {
    return PsiTreeUtil.getStubChildrenOfTypeAsList(this, RsSymField.class);
  }

  @Override
  @NotNull
  public PsiElement getNewLine() {
    return notNullChild(findChildByType(NEW_LINE));
  }

}

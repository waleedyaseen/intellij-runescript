// This class is automatically generated. Do not edit.
package io.runescript.plugin.symbollang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.runescript.plugin.symbollang.psi.RsSymElementTypes.*;
import com.intellij.extapi.psi.StubBasedPsiElementBase;
import io.runescript.plugin.symbollang.psi.stub.RsSymFieldStub;
import io.runescript.plugin.symbollang.psi.*;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;

public class RsSymFieldImpl extends StubBasedPsiElementBase<RsSymFieldStub> implements RsSymField {

  public RsSymFieldImpl(@NotNull RsSymFieldStub stub, @NotNull IStubElementType<?, ?> type) {
    super(stub, type);
  }

  public RsSymFieldImpl(@NotNull ASTNode node) {
    super(node);
  }

  public RsSymFieldImpl(RsSymFieldStub stub, IElementType type, ASTNode node) {
    super(stub, type, node);
  }

  public void accept(@NotNull RsSymVisitor visitor) {
    visitor.visitField(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RsSymVisitor) accept((RsSymVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getString() {
    return notNullChild(findChildByType(STRING));
  }

}

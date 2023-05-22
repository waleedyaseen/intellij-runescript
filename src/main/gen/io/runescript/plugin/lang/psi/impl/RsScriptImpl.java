// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.runescript.plugin.lang.psi.RsElementTypes.*;
import io.runescript.plugin.lang.psi.mixin.RsScriptMixin;
import io.runescript.plugin.lang.psi.*;
import io.runescript.plugin.lang.stubs.RsScriptStub;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;

public class RsScriptImpl extends RsScriptMixin implements RsScript {

  public RsScriptImpl(@NotNull ASTNode node) {
    super(node);
  }

  public RsScriptImpl(@NotNull RsScriptStub stub, @NotNull IStubElementType<?, ?> type) {
    super(stub, type);
  }

  public RsScriptImpl(@Nullable RsScriptStub stub, @Nullable IElementType type, @Nullable ASTNode node) {
    super(stub, type, node);
  }

  public void accept(@NotNull RsVisitor visitor) {
    visitor.visitScript(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RsVisitor) accept((RsVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public RsScriptHeader getScriptHeader() {
    return notNullChild(PsiTreeUtil.getStubChildOfType(this, RsScriptHeader.class));
  }

  @Override
  @NotNull
  public RsStatementList getStatementList() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, RsStatementList.class));
  }

}

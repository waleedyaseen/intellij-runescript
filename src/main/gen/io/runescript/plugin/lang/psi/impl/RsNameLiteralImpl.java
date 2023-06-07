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
import io.runescript.plugin.lang.stubs.RsNameLiteralStub;
import io.runescript.plugin.lang.psi.*;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;

public class RsNameLiteralImpl extends StubBasedPsiElementBase<RsNameLiteralStub> implements RsNameLiteral {

  public RsNameLiteralImpl(@NotNull RsNameLiteralStub stub, @NotNull IStubElementType<?, ?> type) {
    super(stub, type);
  }

  public RsNameLiteralImpl(@NotNull ASTNode node) {
    super(node);
  }

  public RsNameLiteralImpl(RsNameLiteralStub stub, IElementType type, ASTNode node) {
    super(stub, type, node);
  }

  public void accept(@NotNull RsVisitor visitor) {
    visitor.visitNameLiteral(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RsVisitor) accept((RsVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PsiElement getArrayTypeLiteral() {
    return findChildByType(ARRAY_TYPE_LITERAL);
  }

  @Override
  @Nullable
  public PsiElement getCase() {
    return findChildByType(CASE);
  }

  @Override
  @Nullable
  public PsiElement getDefineType() {
    return findChildByType(DEFINE_TYPE);
  }

  @Override
  @Nullable
  public PsiElement getFalse() {
    return findChildByType(FALSE);
  }

  @Override
  @Nullable
  public PsiElement getIdentifier() {
    return findChildByType(IDENTIFIER);
  }

  @Override
  @Nullable
  public PsiElement getIf() {
    return findChildByType(IF);
  }

  @Override
  @Nullable
  public PsiElement getNull() {
    return findChildByType(NULL);
  }

  @Override
  @Nullable
  public PsiElement getSwitch() {
    return findChildByType(SWITCH);
  }

  @Override
  @Nullable
  public PsiElement getTrue() {
    return findChildByType(TRUE);
  }

  @Override
  @Nullable
  public PsiElement getTypeLiteral() {
    return findChildByType(TYPE_LITERAL);
  }

  @Override
  @Nullable
  public PsiElement getWhile() {
    return findChildByType(WHILE);
  }

}

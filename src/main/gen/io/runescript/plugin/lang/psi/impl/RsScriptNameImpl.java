// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.runescript.plugin.lang.psi.RsElementTypes.*;
import io.runescript.plugin.lang.psi.mixin.RsScriptNameMixin;
import io.runescript.plugin.lang.psi.*;
import io.runescript.plugin.lang.stubs.RsScriptNameStub;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;

public class RsScriptNameImpl extends RsScriptNameMixin implements RsScriptName {

  public RsScriptNameImpl(@NotNull ASTNode node) {
    super(node);
  }

  public RsScriptNameImpl(@NotNull RsScriptNameStub stub, @NotNull IStubElementType<?, ?> type) {
    super(stub, type);
  }

  public RsScriptNameImpl(@Nullable RsScriptNameStub stub, @Nullable IElementType type, @Nullable ASTNode node) {
    super(stub, type, node);
  }

  public void accept(@NotNull RsVisitor visitor) {
    visitor.visitScriptName(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RsVisitor) accept((RsVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<RsNameLiteral> getNameLiteralList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RsNameLiteral.class);
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

  @Override
  @NotNull
  public RsNameLiteral getTriggerExpression() {
    List<RsNameLiteral> p1 = getNameLiteralList();
    return p1.get(0);
  }

  @Override
  @Nullable
  public RsNameLiteral getNameExpression() {
    List<RsNameLiteral> p1 = getNameLiteralList();
    return p1.size() < 2 ? null : p1.get(1);
  }

}

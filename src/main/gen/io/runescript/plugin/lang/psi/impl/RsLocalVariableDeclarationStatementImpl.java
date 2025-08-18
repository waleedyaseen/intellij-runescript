// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.runescript.plugin.lang.psi.RsElementTypes.*;
import io.runescript.plugin.lang.psi.mixin.RsLocalVariableDeclarationStatementMixin;
import io.runescript.plugin.lang.psi.*;

public class RsLocalVariableDeclarationStatementImpl extends RsLocalVariableDeclarationStatementMixin implements RsLocalVariableDeclarationStatement {

  public RsLocalVariableDeclarationStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RsVisitor visitor) {
    visitor.visitLocalVariableDeclarationStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RsVisitor) accept((RsVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<RsExpression> getExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RsExpression.class);
  }

  @Override
  @NotNull
  public PsiElement getDefineType() {
    return notNullChild(findChildByType(DEFINE_TYPE));
  }

  @Override
  @Nullable
  public PsiElement getEqual() {
    return findChildByType(EQUAL);
  }

  @Override
  @Nullable
  public PsiElement getSemicolon() {
    return findChildByType(SEMICOLON);
  }

  @Override
  @Nullable
  public RsExpression getInitializer() {
    List<RsExpression> p1 = getExpressionList();
    return p1.size() < 2 ? null : p1.get(1);
  }

}

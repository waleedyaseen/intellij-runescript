// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.runescript.plugin.lang.psi.RsElementTypes.*;
import io.runescript.plugin.lang.psi.*;

public class RsIfStatementImpl extends RsStatementImpl implements RsIfStatement {

  public RsIfStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull RsVisitor visitor) {
    visitor.visitIfStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RsVisitor) accept((RsVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public RsExpression getExpression() {
    return PsiTreeUtil.getChildOfType(this, RsExpression.class);
  }

  @Override
  @NotNull
  public List<RsStatement> getStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RsStatement.class);
  }

  @Override
  @Nullable
  public PsiElement getElse() {
    return findChildByType(ELSE);
  }

  @Override
  @NotNull
  public PsiElement getIf() {
    return notNullChild(findChildByType(IF));
  }

  @Override
  @NotNull
  public PsiElement getLparen() {
    return notNullChild(findChildByType(LPAREN));
  }

  @Override
  @Nullable
  public PsiElement getRparen() {
    return findChildByType(RPAREN);
  }

  @Override
  @Nullable
  public RsStatement getTrueStatement() {
    List<RsStatement> p1 = getStatementList();
    return p1.size() < 1 ? null : p1.get(0);
  }

  @Override
  @Nullable
  public RsStatement getFalseStatement() {
    List<RsStatement> p1 = getStatementList();
    return p1.size() < 2 ? null : p1.get(1);
  }

}

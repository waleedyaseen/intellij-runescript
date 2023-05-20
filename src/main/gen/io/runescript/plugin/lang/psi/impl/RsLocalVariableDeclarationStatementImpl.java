// This is a generated file. Not intended for manual editing.
package io.runescript.plugin.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.runescript.plugin.lang.psi.RsTypes.*;
import io.runescript.plugin.lang.psi.*;

public class RsLocalVariableDeclarationStatementImpl extends RsStatementImpl implements RsLocalVariableDeclarationStatement {

  public RsLocalVariableDeclarationStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
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
  public RsLocalVariableExpression getNameExpression() {
    return RsPsiImplUtil.getNameExpression(this);
  }

}

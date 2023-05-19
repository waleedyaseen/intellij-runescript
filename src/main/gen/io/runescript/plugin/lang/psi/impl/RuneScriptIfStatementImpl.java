// This is a generated file. Not intended for manual editing.
package io.runescript.plugin.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.runescript.plugin.lang.psi.RuneScriptTypes.*;
import io.runescript.plugin.lang.psi.*;

public class RuneScriptIfStatementImpl extends RuneScriptStatementImpl implements RuneScriptIfStatement {

  public RuneScriptIfStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull RuneScriptVisitor visitor) {
    visitor.visitIfStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RuneScriptVisitor) accept((RuneScriptVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public RuneScriptExpression getExpression() {
    return findNotNullChildByClass(RuneScriptExpression.class);
  }

  @Override
  @NotNull
  public RuneScriptStatement getStatement() {
    return findNotNullChildByClass(RuneScriptStatement.class);
  }

}

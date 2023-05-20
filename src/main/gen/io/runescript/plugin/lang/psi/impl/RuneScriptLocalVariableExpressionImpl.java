// This is a generated file. Not intended for manual editing.
package io.runescript.plugin.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.runescript.plugin.lang.psi.RuneScriptTypes.*;
import io.runescript.plugin.lang.psi.impl.naned.RuneScriptNamedElementImpl;
import io.runescript.plugin.lang.psi.*;

public class RuneScriptLocalVariableExpressionImpl extends RuneScriptNamedElementImpl implements RuneScriptLocalVariableExpression {

  public RuneScriptLocalVariableExpressionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RuneScriptVisitor visitor) {
    visitor.visitLocalVariableExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RuneScriptVisitor) accept((RuneScriptVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public RuneScriptNameLiteral getNameLiteral() {
    return findNotNullChildByClass(RuneScriptNameLiteral.class);
  }

  @Override
  @NotNull
  public String getName() {
    return RuneScriptPsiImplUtil.getName(this);
  }

  @Override
  @NotNull
  public PsiElement setName(@NotNull String newName) {
    return RuneScriptPsiImplUtil.setName(this, newName);
  }

  @Override
  @NotNull
  public PsiElement getNameIdentifier() {
    return RuneScriptPsiImplUtil.getNameIdentifier(this);
  }

}

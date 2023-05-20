// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.runescript.plugin.lang.psi.RsTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import io.runescript.plugin.lang.psi.*;

public class RsArithmeticOpImpl extends ASTWrapperPsiElement implements RsArithmeticOp {

  public RsArithmeticOpImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RsVisitor visitor) {
    visitor.visitArithmeticOp(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RsVisitor) accept((RsVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PsiElement getAmpersand() {
    return findChildByType(AMPERSAND);
  }

  @Override
  @Nullable
  public PsiElement getBar() {
    return findChildByType(BAR);
  }

  @Override
  @Nullable
  public PsiElement getMinus() {
    return findChildByType(MINUS);
  }

  @Override
  @Nullable
  public PsiElement getPercent() {
    return findChildByType(PERCENT);
  }

  @Override
  @Nullable
  public PsiElement getPlus() {
    return findChildByType(PLUS);
  }

  @Override
  @Nullable
  public PsiElement getSlash() {
    return findChildByType(SLASH);
  }

  @Override
  @Nullable
  public PsiElement getStar() {
    return findChildByType(STAR);
  }

}

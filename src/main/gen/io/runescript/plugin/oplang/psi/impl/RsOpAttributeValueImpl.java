// This class is automatically generated. Do not edit.
package io.runescript.plugin.oplang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.runescript.plugin.oplang.psi.RsOpElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import io.runescript.plugin.oplang.psi.*;

public class RsOpAttributeValueImpl extends ASTWrapperPsiElement implements RsOpAttributeValue {

  public RsOpAttributeValueImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RsOpVisitor visitor) {
    visitor.visitAttributeValue(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RsOpVisitor) accept((RsOpVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public RsOpIntegerValue getIntegerValue() {
    return PsiTreeUtil.getChildOfType(this, RsOpIntegerValue.class);
  }

  @Override
  @Nullable
  public RsOpNameLiteral getNameLiteral() {
    return PsiTreeUtil.getChildOfType(this, RsOpNameLiteral.class);
  }

}

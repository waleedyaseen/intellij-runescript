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

public class RsScriptHeaderImpl extends ASTWrapperPsiElement implements RsScriptHeader {

  public RsScriptHeaderImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RsVisitor visitor) {
    visitor.visitScriptHeader(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RsVisitor) accept((RsVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public RsParameterList getParameterList() {
    return findChildByClass(RsParameterList.class);
  }

  @Override
  @Nullable
  public RsReturnList getReturnList() {
    return findChildByClass(RsReturnList.class);
  }

  @Override
  @NotNull
  public RsScriptName getScriptName() {
    return findNotNullChildByClass(RsScriptName.class);
  }

}

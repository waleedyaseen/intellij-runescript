// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi.impl.op;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.runescript.plugin.lang.psi.op.RsOpElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import io.runescript.plugin.lang.psi.op.*;

public class RsOpAttributeImpl extends ASTWrapperPsiElement implements RsOpAttribute {

  public RsOpAttributeImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RsOpVisitor visitor) {
    visitor.visitAttribute(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RsOpVisitor) accept((RsOpVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<RsOpAttributeValue> getAttributeValueList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RsOpAttributeValue.class);
  }

  @Override
  @NotNull
  public RsOpNameLiteral getNameLiteral() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, RsOpNameLiteral.class));
  }

  @Override
  @NotNull
  public PsiElement getHash() {
    return notNullChild(findChildByType(HASH));
  }

  @Override
  @NotNull
  public PsiElement getLbracket() {
    return notNullChild(findChildByType(LBRACKET));
  }

  @Override
  @Nullable
  public PsiElement getLparen() {
    return findChildByType(LPAREN);
  }

  @Override
  @NotNull
  public PsiElement getRbracket() {
    return notNullChild(findChildByType(RBRACKET));
  }

  @Override
  @Nullable
  public PsiElement getRparen() {
    return findChildByType(RPAREN);
  }

}

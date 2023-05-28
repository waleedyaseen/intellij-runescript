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

public class RsOpIntegerValueImpl extends ASTWrapperPsiElement implements RsOpIntegerValue {

  public RsOpIntegerValueImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RsOpVisitor visitor) {
    visitor.visitIntegerValue(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RsOpVisitor) accept((RsOpVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getInteger() {
    return notNullChild(findChildByType(INTEGER));
  }

}

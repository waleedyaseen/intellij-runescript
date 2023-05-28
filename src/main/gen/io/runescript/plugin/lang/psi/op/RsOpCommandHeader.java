// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi.op;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RsOpCommandHeader extends PsiElement {

  @NotNull
  List<RsOpNameLiteral> getNameLiteralList();

  @NotNull
  PsiElement getComma();

  @NotNull
  PsiElement getLbracket();

  @NotNull
  PsiElement getRbracket();

}

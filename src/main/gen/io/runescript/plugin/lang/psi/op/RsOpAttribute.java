// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi.op;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RsOpAttribute extends PsiElement {

  @NotNull
  List<RsOpAttributeValue> getAttributeValueList();

  @NotNull
  RsOpNameLiteral getNameLiteral();

  @NotNull
  PsiElement getHash();

  @NotNull
  PsiElement getLbracket();

  @Nullable
  PsiElement getLparen();

  @NotNull
  PsiElement getRbracket();

  @Nullable
  PsiElement getRparen();

}

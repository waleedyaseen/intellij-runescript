// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi.op;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RsOpParameterList extends PsiElement {

  @NotNull
  List<RsOpTypeName> getTypeNameList();

  @NotNull
  PsiElement getLparen();

  @NotNull
  PsiElement getRparen();

}

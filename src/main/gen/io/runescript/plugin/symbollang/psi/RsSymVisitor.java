// This class is automatically generated. Do not edit.
package io.runescript.plugin.symbollang.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;
import io.runescript.plugin.lang.psi.RsNamedElement;

public class RsSymVisitor extends PsiElementVisitor {

  public void visitField(@NotNull RsSymField o) {
    visitPsiElement(o);
  }

  public void visitSymbol(@NotNull RsSymSymbol o) {
    visitRsNamedElement(o);
  }

  public void visitRsNamedElement(@NotNull RsNamedElement o) {
    visitElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}

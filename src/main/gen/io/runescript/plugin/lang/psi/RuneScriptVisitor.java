// This is a generated file. Not intended for manual editing.
package io.runescript.plugin.lang.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class RuneScriptVisitor extends PsiElementVisitor {

  public void visitParameter(@NotNull RuneScriptParameter o) {
    visitPsiElement(o);
  }

  public void visitParameterList(@NotNull RuneScriptParameterList o) {
    visitPsiElement(o);
  }

  public void visitReturnList(@NotNull RuneScriptReturnList o) {
    visitPsiElement(o);
  }

  public void visitScript(@NotNull RuneScriptScript o) {
    visitPsiElement(o);
  }

  public void visitScriptHeader(@NotNull RuneScriptScriptHeader o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}

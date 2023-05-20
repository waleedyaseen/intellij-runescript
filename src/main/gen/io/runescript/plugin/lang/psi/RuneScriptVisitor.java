// This is a generated file. Not intended for manual editing.
package io.runescript.plugin.lang.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;
import io.runescript.plugin.lang.psi.named.RuneScriptNamedElement;

public class RuneScriptVisitor extends PsiElementVisitor {

  public void visitArithmeticAdditiveExpression(@NotNull RuneScriptArithmeticAdditiveExpression o) {
    visitExpression(o);
  }

  public void visitArithmeticBitwiseAndExpression(@NotNull RuneScriptArithmeticBitwiseAndExpression o) {
    visitExpression(o);
  }

  public void visitArithmeticBitwiseOrExpression(@NotNull RuneScriptArithmeticBitwiseOrExpression o) {
    visitExpression(o);
  }

  public void visitArithmeticExpression(@NotNull RuneScriptArithmeticExpression o) {
    visitExpression(o);
  }

  public void visitArithmeticMultiplicativeExpression(@NotNull RuneScriptArithmeticMultiplicativeExpression o) {
    visitExpression(o);
  }

  public void visitArithmeticValueExpression(@NotNull RuneScriptArithmeticValueExpression o) {
    visitExpression(o);
  }

  public void visitArrayVariableAssignmentStatement(@NotNull RuneScriptArrayVariableAssignmentStatement o) {
    visitStatement(o);
  }

  public void visitArrayVariableDeclarationStatement(@NotNull RuneScriptArrayVariableDeclarationStatement o) {
    visitStatement(o);
  }

  public void visitArrayVariableExpression(@NotNull RuneScriptArrayVariableExpression o) {
    visitExpression(o);
  }

  public void visitBlockStatement(@NotNull RuneScriptBlockStatement o) {
    visitStatement(o);
  }

  public void visitBooleanLiteralExpression(@NotNull RuneScriptBooleanLiteralExpression o) {
    visitExpression(o);
  }

  public void visitCalcExpression(@NotNull RuneScriptCalcExpression o) {
    visitExpression(o);
  }

  public void visitCommandExpression(@NotNull RuneScriptCommandExpression o) {
    visitExpression(o);
  }

  public void visitCompareExpression(@NotNull RuneScriptCompareExpression o) {
    visitExpression(o);
  }

  public void visitConstantExpression(@NotNull RuneScriptConstantExpression o) {
    visitExpression(o);
  }

  public void visitDynamicExpression(@NotNull RuneScriptDynamicExpression o) {
    visitExpression(o);
  }

  public void visitExpression(@NotNull RuneScriptExpression o) {
    visitPsiElement(o);
  }

  public void visitExpressionStatement(@NotNull RuneScriptExpressionStatement o) {
    visitStatement(o);
  }

  public void visitGosubExpression(@NotNull RuneScriptGosubExpression o) {
    visitExpression(o);
  }

  public void visitIfStatement(@NotNull RuneScriptIfStatement o) {
    visitStatement(o);
  }

  public void visitIntegerLiteralExpression(@NotNull RuneScriptIntegerLiteralExpression o) {
    visitExpression(o);
  }

  public void visitLocalVariableAssignmentStatement(@NotNull RuneScriptLocalVariableAssignmentStatement o) {
    visitStatement(o);
  }

  public void visitLocalVariableDeclarationStatement(@NotNull RuneScriptLocalVariableDeclarationStatement o) {
    visitStatement(o);
  }

  public void visitLocalVariableExpression(@NotNull RuneScriptLocalVariableExpression o) {
    visitExpression(o);
    // visitNamedElement(o);
  }

  public void visitLogicalAndExpression(@NotNull RuneScriptLogicalAndExpression o) {
    visitExpression(o);
  }

  public void visitLogicalOrExpression(@NotNull RuneScriptLogicalOrExpression o) {
    visitExpression(o);
  }

  public void visitNameLiteral(@NotNull RuneScriptNameLiteral o) {
    visitPsiElement(o);
  }

  public void visitNullLiteralExpression(@NotNull RuneScriptNullLiteralExpression o) {
    visitExpression(o);
  }

  public void visitParExpression(@NotNull RuneScriptParExpression o) {
    visitExpression(o);
  }

  public void visitParameter(@NotNull RuneScriptParameter o) {
    visitPsiElement(o);
  }

  public void visitParameterList(@NotNull RuneScriptParameterList o) {
    visitPsiElement(o);
  }

  public void visitRelationalValueExpression(@NotNull RuneScriptRelationalValueExpression o) {
    visitExpression(o);
  }

  public void visitReturnList(@NotNull RuneScriptReturnList o) {
    visitPsiElement(o);
  }

  public void visitReturnStatement(@NotNull RuneScriptReturnStatement o) {
    visitStatement(o);
  }

  public void visitScopedVariableAssignmentStatement(@NotNull RuneScriptScopedVariableAssignmentStatement o) {
    visitStatement(o);
  }

  public void visitScopedVariableExpression(@NotNull RuneScriptScopedVariableExpression o) {
    visitExpression(o);
  }

  public void visitScript(@NotNull RuneScriptScript o) {
    visitPsiElement(o);
  }

  public void visitScriptHeader(@NotNull RuneScriptScriptHeader o) {
    visitPsiElement(o);
  }

  public void visitScriptName(@NotNull RuneScriptScriptName o) {
    visitPsiElement(o);
  }

  public void visitStatement(@NotNull RuneScriptStatement o) {
    visitPsiElement(o);
  }

  public void visitStatementList(@NotNull RuneScriptStatementList o) {
    visitPsiElement(o);
  }

  public void visitStringInterpolationExpression(@NotNull RuneScriptStringInterpolationExpression o) {
    visitExpression(o);
  }

  public void visitStringLiteralExpression(@NotNull RuneScriptStringLiteralExpression o) {
    visitExpression(o);
  }

  public void visitSwitchCase(@NotNull RuneScriptSwitchCase o) {
    visitPsiElement(o);
  }

  public void visitSwitchStatement(@NotNull RuneScriptSwitchStatement o) {
    visitStatement(o);
  }

  public void visitWhileStatement(@NotNull RuneScriptWhileStatement o) {
    visitStatement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
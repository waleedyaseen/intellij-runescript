// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;
import io.runescript.plugin.lang.psi.named.RsNamedElement;

public class RsVisitor extends PsiElementVisitor {

  public void visitArithmeticAdditiveExpression(@NotNull RsArithmeticAdditiveExpression o) {
    visitExpression(o);
  }

  public void visitArithmeticBitwiseAndExpression(@NotNull RsArithmeticBitwiseAndExpression o) {
    visitExpression(o);
  }

  public void visitArithmeticBitwiseOrExpression(@NotNull RsArithmeticBitwiseOrExpression o) {
    visitExpression(o);
  }

  public void visitArithmeticExpression(@NotNull RsArithmeticExpression o) {
    visitExpression(o);
  }

  public void visitArithmeticMultiplicativeExpression(@NotNull RsArithmeticMultiplicativeExpression o) {
    visitExpression(o);
  }

  public void visitArithmeticValueExpression(@NotNull RsArithmeticValueExpression o) {
    visitExpression(o);
  }

  public void visitArrayVariableAssignmentStatement(@NotNull RsArrayVariableAssignmentStatement o) {
    visitStatement(o);
  }

  public void visitArrayVariableDeclarationStatement(@NotNull RsArrayVariableDeclarationStatement o) {
    visitStatement(o);
  }

  public void visitArrayVariableExpression(@NotNull RsArrayVariableExpression o) {
    visitExpression(o);
  }

  public void visitBlockStatement(@NotNull RsBlockStatement o) {
    visitStatement(o);
  }

  public void visitBooleanLiteralExpression(@NotNull RsBooleanLiteralExpression o) {
    visitExpression(o);
  }

  public void visitCalcExpression(@NotNull RsCalcExpression o) {
    visitExpression(o);
  }

  public void visitCommandExpression(@NotNull RsCommandExpression o) {
    visitExpression(o);
  }

  public void visitCompareExpression(@NotNull RsCompareExpression o) {
    visitExpression(o);
  }

  public void visitConstantExpression(@NotNull RsConstantExpression o) {
    visitExpression(o);
  }

  public void visitDynamicExpression(@NotNull RsDynamicExpression o) {
    visitExpression(o);
  }

  public void visitExpression(@NotNull RsExpression o) {
    visitPsiElement(o);
  }

  public void visitExpressionStatement(@NotNull RsExpressionStatement o) {
    visitStatement(o);
  }

  public void visitGosubExpression(@NotNull RsGosubExpression o) {
    visitExpression(o);
  }

  public void visitIfStatement(@NotNull RsIfStatement o) {
    visitStatement(o);
  }

  public void visitIntegerLiteralExpression(@NotNull RsIntegerLiteralExpression o) {
    visitExpression(o);
  }

  public void visitLocalVariableAssignmentStatement(@NotNull RsLocalVariableAssignmentStatement o) {
    visitStatement(o);
  }

  public void visitLocalVariableDeclarationStatement(@NotNull RsLocalVariableDeclarationStatement o) {
    visitStatement(o);
  }

  public void visitLocalVariableExpression(@NotNull RsLocalVariableExpression o) {
    visitExpression(o);
    // visitNamedElement(o);
  }

  public void visitLogicalAndExpression(@NotNull RsLogicalAndExpression o) {
    visitExpression(o);
  }

  public void visitLogicalOrExpression(@NotNull RsLogicalOrExpression o) {
    visitExpression(o);
  }

  public void visitNameLiteral(@NotNull RsNameLiteral o) {
    visitPsiElement(o);
  }

  public void visitNullLiteralExpression(@NotNull RsNullLiteralExpression o) {
    visitExpression(o);
  }

  public void visitParExpression(@NotNull RsParExpression o) {
    visitExpression(o);
  }

  public void visitParameterList(@NotNull RsParameterList o) {
    visitPsiElement(o);
  }

  public void visitRelationalValueExpression(@NotNull RsRelationalValueExpression o) {
    visitExpression(o);
  }

  public void visitReturnList(@NotNull RsReturnList o) {
    visitPsiElement(o);
  }

  public void visitReturnStatement(@NotNull RsReturnStatement o) {
    visitStatement(o);
  }

  public void visitScopedVariableAssignmentStatement(@NotNull RsScopedVariableAssignmentStatement o) {
    visitStatement(o);
  }

  public void visitScopedVariableExpression(@NotNull RsScopedVariableExpression o) {
    visitExpression(o);
  }

  public void visitScript(@NotNull RsScript o) {
    visitPsiElement(o);
  }

  public void visitScriptHeader(@NotNull RsScriptHeader o) {
    visitPsiElement(o);
  }

  public void visitScriptName(@NotNull RsScriptName o) {
    visitPsiElement(o);
  }

  public void visitStatement(@NotNull RsStatement o) {
    visitPsiElement(o);
  }

  public void visitStatementList(@NotNull RsStatementList o) {
    visitPsiElement(o);
  }

  public void visitStringInterpolationExpression(@NotNull RsStringInterpolationExpression o) {
    visitExpression(o);
  }

  public void visitStringLiteralExpression(@NotNull RsStringLiteralExpression o) {
    visitExpression(o);
  }

  public void visitSwitchCase(@NotNull RsSwitchCase o) {
    visitPsiElement(o);
  }

  public void visitSwitchStatement(@NotNull RsSwitchStatement o) {
    visitStatement(o);
  }

  public void visitWhileStatement(@NotNull RsWhileStatement o) {
    visitStatement(o);
  }

  public void visitParameter(@NotNull RsParameter o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}

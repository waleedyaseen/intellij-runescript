// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.NavigatablePsiElement;
import io.runescript.plugin.lang.psi.type.inference.RsInferenceDataHolder;
import com.intellij.psi.PsiLanguageInjectionHost;

public class RsVisitor extends PsiElementVisitor {

  public void visitArgumentList(@NotNull RsArgumentList o) {
    visitPsiElement(o);
  }

  public void visitArithmeticExpression(@NotNull RsArithmeticExpression o) {
    visitExpression(o);
    // visitBinaryExpression(o);
  }

  public void visitArithmeticOp(@NotNull RsArithmeticOp o) {
    visitPsiElement(o);
  }

  public void visitArithmeticValueExpression(@NotNull RsArithmeticValueExpression o) {
    visitExpression(o);
  }

  public void visitArrayAccessExpression(@NotNull RsArrayAccessExpression o) {
    visitExpression(o);
  }

  public void visitArrayVariableDeclarationStatement(@NotNull RsArrayVariableDeclarationStatement o) {
    visitStatement(o);
  }

  public void visitAssignmentStatement(@NotNull RsAssignmentStatement o) {
    visitStatement(o);
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
    // visitNavigatablePsiElement(o);
  }

  public void visitConditionExpression(@NotNull RsConditionExpression o) {
    visitExpression(o);
    // visitBinaryExpression(o);
  }

  public void visitConditionOp(@NotNull RsConditionOp o) {
    visitPsiElement(o);
  }

  public void visitConstantExpression(@NotNull RsConstantExpression o) {
    visitExpression(o);
    // visitNamedElement(o);
  }

  public void visitCoordLiteralExpression(@NotNull RsCoordLiteralExpression o) {
    visitExpression(o);
  }

  public void visitDynamicExpression(@NotNull RsDynamicExpression o) {
    visitExpression(o);
    // visitNamedElement(o);
  }

  public void visitEmptyStatement(@NotNull RsEmptyStatement o) {
    visitStatement(o);
  }

  public void visitExpression(@NotNull RsExpression o) {
    visitPsiElement(o);
  }

  public void visitExpressionStatement(@NotNull RsExpressionStatement o) {
    visitStatement(o);
  }

  public void visitGosubExpression(@NotNull RsGosubExpression o) {
    visitExpression(o);
    // visitNamedElement(o);
  }

  public void visitHookFragment(@NotNull RsHookFragment o) {
    visitNamedElement(o);
    // visitInferenceDataHolder(o);
  }

  public void visitHookRoot(@NotNull RsHookRoot o) {
    visitPsiElement(o);
  }

  public void visitHookTransmitList(@NotNull RsHookTransmitList o) {
    visitPsiElement(o);
  }

  public void visitIfStatement(@NotNull RsIfStatement o) {
    visitStatement(o);
  }

  public void visitIntegerLiteralExpression(@NotNull RsIntegerLiteralExpression o) {
    visitExpression(o);
  }

  public void visitLocalVariableDeclarationStatement(@NotNull RsLocalVariableDeclarationStatement o) {
    visitStatement(o);
  }

  public void visitLocalVariableExpression(@NotNull RsLocalVariableExpression o) {
    visitExpression(o);
    // visitNamedElement(o);
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

  public void visitParameter(@NotNull RsParameter o) {
    visitPsiElement(o);
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

  public void visitScopedVariableExpression(@NotNull RsScopedVariableExpression o) {
    visitExpression(o);
    // visitNamedElement(o);
  }

  public void visitScript(@NotNull RsScript o) {
    visitNavigatablePsiElement(o);
    // visitControlFlowHolder(o);
    // visitNamedElement(o);
    // visitInferenceDataHolder(o);
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

  public void visitStringLiteralContent(@NotNull RsStringLiteralContent o) {
    visitPsiLanguageInjectionHost(o);
  }

  public void visitStringLiteralExpression(@NotNull RsStringLiteralExpression o) {
    visitExpression(o);
    // visitNamedElement(o);
  }

  public void visitSwitchCase(@NotNull RsSwitchCase o) {
    visitPsiElement(o);
  }

  public void visitSwitchCaseDefaultExpression(@NotNull RsSwitchCaseDefaultExpression o) {
    visitExpression(o);
  }

  public void visitSwitchStatement(@NotNull RsSwitchStatement o) {
    visitStatement(o);
  }

  public void visitTypeName(@NotNull RsTypeName o) {
    visitPsiElement(o);
  }

  public void visitWhileStatement(@NotNull RsWhileStatement o) {
    visitStatement(o);
  }

  public void visitNavigatablePsiElement(@NotNull NavigatablePsiElement o) {
    visitElement(o);
  }

  public void visitPsiLanguageInjectionHost(@NotNull PsiLanguageInjectionHost o) {
    visitElement(o);
  }

  public void visitNamedElement(@NotNull RsNamedElement o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}

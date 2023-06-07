package io.runescript.plugin.lang.psi

import com.intellij.psi.PsiElement
import com.intellij.refactoring.suggested.startOffset

object RsPsiImplUtil {

    @JvmStatic
    fun getName(element: RsLocalVariableExpression): String? {
        return element.nameLiteral.text
    }

    @JvmStatic
    fun setName(element: RsLocalVariableExpression, newName: String): PsiElement {
        val nameLiteralNode = element.nameLiteral.node
        val newNameLiteral = RsElementGenerator.createNameLiteral(element.project, newName)
        val newNameLiteralNode = newNameLiteral.firstChild.node
        element.node.replaceChild(nameLiteralNode, newNameLiteralNode)
        return newNameLiteral
    }

    @JvmStatic
    fun getNameExpression(element: RsLocalVariableDeclarationStatement): RsLocalVariableExpression {
        return element.expressionList[0] as RsLocalVariableExpression
    }

    @JvmStatic
    fun getNameIdentifier(element: RsLocalVariableExpression): PsiElement {
        return element.nameLiteral
    }

    @JvmStatic
    fun getLeftExpressions(statement: RsAssignmentStatement): List<RsExpression> {
        return statement.expressionList.takeWhile { it.startOffset < statement.equal.startOffset }
    }

    @JvmStatic
    fun getRightExpressions(statement: RsAssignmentStatement): List<RsExpression> {
        return statement.expressionList.takeWhile { it.startOffset > statement.equal.startOffset }
    }
}
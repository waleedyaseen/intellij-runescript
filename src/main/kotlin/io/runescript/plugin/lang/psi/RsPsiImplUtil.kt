package io.runescript.plugin.lang.psi

import com.intellij.psi.PsiElement
import com.intellij.refactoring.suggested.startOffset

object RsPsiImplUtil {

    @JvmStatic
    fun getNameExpression(element: RsLocalVariableDeclarationStatement): RsLocalVariableExpression {
        return element.expressionList[0] as RsLocalVariableExpression
    }

    @JvmStatic
    fun getLeftExpressions(statement: RsAssignmentStatement): List<RsExpression> {
        return statement.expressionList.takeWhile { it.startOffset < statement.equal.startOffset }
    }

    @JvmStatic
    fun getRightExpressions(statement: RsAssignmentStatement): List<RsExpression> {
        return statement.expressionList.takeWhile { it.startOffset > statement.equal.startOffset }
    }

    @JvmStatic
    fun getName(element: RsNameLiteral): String {
        return element.text
    }

    @JvmStatic
    fun setName(element: RsNameLiteral, newName: String): PsiElement {
        val newNameLiteral = RsElementGenerator.createNameLiteral(element.project, newName)
        return element.replace(newNameLiteral)
    }
}
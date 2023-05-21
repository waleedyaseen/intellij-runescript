package io.runescript.plugin.lang.psi

import com.intellij.psi.PsiElement

object RsPsiImplUtil {

    @JvmStatic
    fun getVariableName(element: RsLocalVariableExpression): String {
        return element.nameLiteral.text
    }

    @JvmStatic
    fun setVariableName(element: RsLocalVariableExpression, newName: String): PsiElement {
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
}
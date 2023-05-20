package io.runescript.plugin.lang.psi

import com.intellij.psi.PsiElement

object RuneScriptPsiImplUtil {

    @JvmStatic
    fun getName(element: RuneScriptLocalVariableExpression): String {
        return element.nameLiteral.text
    }

    @JvmStatic
    fun setName(element: RuneScriptLocalVariableExpression, newName: String): PsiElement {
        val nameLiteralNode = element.nameLiteral.node
        val newNameLiteral = RuneScriptElementGenerator.createNameLiteral(element.project, newName)
        val newNameLiteralNode = newNameLiteral.firstChild.node
        element.node.replaceChild(nameLiteralNode, newNameLiteralNode)
        return newNameLiteral
    }

    @JvmStatic
    fun getNameExpression(element: RuneScriptLocalVariableDeclarationStatement): RuneScriptLocalVariableExpression {
        return element.expressionList[0] as RuneScriptLocalVariableExpression
    }

    @JvmStatic
    fun getNameIdentifier(element: RuneScriptLocalVariableExpression): PsiElement {
        return element.nameLiteral
    }
}
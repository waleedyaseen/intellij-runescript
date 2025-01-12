package io.runescript.plugin.lang.psi.mixin

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.util.startOffset
import io.runescript.plugin.lang.psi.RsElementGenerator
import io.runescript.plugin.lang.psi.RsStringLiteralExpression
import io.runescript.plugin.lang.psi.isHookExpression
import io.runescript.plugin.lang.psi.refs.RsStringLiteralReference
import io.runescript.plugin.lang.psi.type.RsPrimitiveType
import io.runescript.plugin.lang.psi.type.type

abstract class RsStringLiteralExpressionMixin(node: ASTNode) : ASTWrapperPsiElement(node), RsStringLiteralExpression {

    override fun getReference(): RsStringLiteralReference? {
        if (stringLiteralContent.isHookExpression()) {
            return null
        }
        if (type is RsPrimitiveType && type != RsPrimitiveType.STRING) {
            return RsStringLiteralReference(this)
        }
        return null
    }

    override fun getTextOffset(): Int {
        if (stringLiteralContent.isHookExpression()) {
            return -1
        }
        return stringLiteralContent.startOffset
    }

    override fun getNameIdentifier(): PsiElement? {
        if (stringLiteralContent.isHookExpression()) {
            return null
        }
        return stringLiteralContent
    }

    override fun getName(): String? {
        if (stringLiteralContent.isHookExpression()) {
            return null
        }
        return stringLiteralContent.text
    }

    override fun setName(name: String): PsiElement {
        if (stringLiteralContent.isHookExpression()) {
            return this
        }
        val newStringLiteralContent = RsElementGenerator.createStringLiteralContent(project, name)
        return stringLiteralContent.replace(newStringLiteralContent)
    }
}
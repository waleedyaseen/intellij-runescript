package io.runescript.plugin.lang.psi.mixin

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.refactoring.suggested.startOffset
import io.runescript.plugin.lang.psi.RsElementGenerator
import io.runescript.plugin.lang.psi.RsStringLiteralExpression
import io.runescript.plugin.lang.psi.refs.RsStringLiteralReference
import io.runescript.plugin.lang.psi.type.RsPrimitiveType
import io.runescript.plugin.lang.psi.type.type

abstract class RsStringLiteralExpressionMixin(node: ASTNode) : ASTWrapperPsiElement(node), RsStringLiteralExpression {

    override fun getReference(): RsStringLiteralReference? {
        if (type is RsPrimitiveType && type != RsPrimitiveType.STRING) {
            return RsStringLiteralReference(this)
        }
        return null
    }

    override fun getTextOffset(): Int {
        return stringLiteralContent.startOffset
    }

    override fun getNameIdentifier(): PsiElement {
        return stringLiteralContent
    }

    override fun getName(): String {
        return stringLiteralContent.text
    }

    override fun setName(name: String): PsiElement {
        val newStringLiteralContent = RsElementGenerator.createStringLiteralContent(project, name)
        return stringLiteralContent.replace(newStringLiteralContent)
    }
}
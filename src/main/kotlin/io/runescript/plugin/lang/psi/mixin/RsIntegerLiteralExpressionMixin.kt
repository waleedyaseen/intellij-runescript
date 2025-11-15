package io.runescript.plugin.lang.psi.mixin

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import io.runescript.plugin.lang.psi.RsElementGenerator
import io.runescript.plugin.lang.psi.RsIntegerLiteralExpression
import io.runescript.plugin.lang.psi.refs.RsIntegerLiteralReference
import io.runescript.plugin.lang.psi.typechecker.type.PrimitiveType
import io.runescript.plugin.lang.psi.typechecker.typeCheckedType

abstract class RsIntegerLiteralExpressionMixin(node: ASTNode) : ASTWrapperPsiElement(node), RsIntegerLiteralExpression {

    override fun getReference(): RsIntegerLiteralReference? {
        if (typeCheckedType != PrimitiveType.INT) {
            return RsIntegerLiteralReference(this)
        }
        return null
    }

    override fun getNameIdentifier(): PsiElement? {
        return this
    }

    override fun getName(): String? {
        return text
    }

    override fun setName(name: String): PsiElement {
        val replacement = RsElementGenerator.createExpression(project, name)
        return replace(replacement)
    }
}
package io.runescript.plugin.lang.psi

import com.intellij.psi.PsiElement

object RsPsiImplUtil {

    @JvmStatic
    fun getName(element: RsNameLiteral): String {
        return element.text
    }

    @JvmStatic
    fun setName(element: RsNameLiteral, newName: String): PsiElement {
        val newNameLiteral = RsElementGenerator.createNameLiteral(element.project, newName)
        return element.replace(newNameLiteral)
    }

    /**
     * Whether the call is a star command call or not. This means that another
     * set of arguments were supplied.
     */
    @JvmStatic
    val RsCommandExpression.isStar: Boolean
        get() = args2 != null

    /**
     * The name of the command being called. This will be the same as `name.text` except when
     * [isStar] is true, in which case this will be `{name.text}*`.
     */
    @JvmStatic
    val RsCommandExpression.nameString: String
        get() = if (isStar) "${nameLiteral.text}*" else nameLiteral.text
}
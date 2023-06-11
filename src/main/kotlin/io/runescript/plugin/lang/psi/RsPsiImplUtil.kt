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
}
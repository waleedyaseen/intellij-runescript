package io.runescript.plugin.symbollang.psi

import com.intellij.psi.PsiElement

object RsSymPsiImplUtil {

    @JvmStatic
    fun getName(element: RsSymField): String {
        return element.text
    }

    @JvmStatic
    fun setName(element: RsSymField, newName: String): PsiElement {
        val newField = RsSymElementGenerator.createField(element.project, newName)
        return element.replace(newField)
    }
}
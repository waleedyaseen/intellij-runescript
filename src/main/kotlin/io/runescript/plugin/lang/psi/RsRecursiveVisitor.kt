package io.runescript.plugin.lang.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiRecursiveVisitor

abstract class RsRecursiveVisitor : RsVisitor(), PsiRecursiveVisitor {

    override fun visitElement(element: PsiElement) {
        super.visitElement(element)
        element.acceptChildren(this)
    }
}
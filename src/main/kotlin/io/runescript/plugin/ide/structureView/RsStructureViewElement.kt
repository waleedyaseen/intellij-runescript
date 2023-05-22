package io.runescript.plugin.ide.structureView

import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.util.childrenOfType
import io.runescript.plugin.lang.psi.RsFile
import io.runescript.plugin.lang.psi.RsScript

class RsStructureViewElement(val element: NavigatablePsiElement) : PsiTreeElementBase<NavigatablePsiElement>(element) {

    private val myPresentation: ItemPresentation by lazy {
        element.presentation!!
    }

    override fun getPresentation(): ItemPresentation {
        return myPresentation
    }

    override fun getPresentableText(): String? {
        return myPresentation.presentableText
    }

    override fun getChildrenBase(): Collection<StructureViewTreeElement> {
        val rsFile = element as? RsFile ?: return emptyList()
        return rsFile
                .childrenOfType<RsScript>()
                .map { RsStructureViewElement(it) }
    }
}
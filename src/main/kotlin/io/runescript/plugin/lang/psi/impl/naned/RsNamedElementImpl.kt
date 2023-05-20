package io.runescript.plugin.lang.psi.impl.naned

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry
import com.intellij.refactoring.suggested.startOffset
import io.runescript.plugin.lang.psi.named.RsNamedElement

abstract class RsNamedElementImpl(node: ASTNode) : ASTWrapperPsiElement(node), RsNamedElement {

    override fun getReferences(): Array<PsiReference> {
        return ReferenceProvidersRegistry.getReferencesFromProviders(this);
    }

    override fun getTextOffset(): Int {
        return nameIdentifier!!.startOffset
    }
}
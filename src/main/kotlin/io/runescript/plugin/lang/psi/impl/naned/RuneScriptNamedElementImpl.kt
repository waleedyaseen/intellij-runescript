package io.runescript.plugin.lang.psi.impl.naned

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.ContributedReferenceHost
import com.intellij.psi.PsiReference
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry
import io.runescript.plugin.lang.psi.named.RuneScriptNamedElement
import io.runescript.plugin.lang.psi.refs.RuneScriptReference
import org.slf4j.LoggerFactory
import javax.swing.Icon

abstract class RuneScriptNamedElementImpl(node: ASTNode) : ASTWrapperPsiElement(node), RuneScriptNamedElement {

    override fun getReferences(): Array<PsiReference> {
        return ReferenceProvidersRegistry.getReferencesFromProviders(this);
    }
}
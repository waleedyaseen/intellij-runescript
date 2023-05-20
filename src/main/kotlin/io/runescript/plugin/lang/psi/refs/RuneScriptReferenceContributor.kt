package io.runescript.plugin.lang.psi.refs

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.*
import com.intellij.util.ProcessingContext
import io.runescript.plugin.lang.psi.named.RuneScriptNamedElement

class RuneScriptReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns.not(PlatformPatterns.alwaysFalse()),
            RuneScriptReferenceProvider()
        )
    }
}

class RuneScriptReferenceProvider : PsiReferenceProvider() {

    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        if (element is RuneScriptNamedElement) {
            val namedIdentifier = element.nameIdentifier!!
            return arrayOf(RuneScriptReference(element, namedIdentifier.textRangeInParent))
        }
        return PsiReference.EMPTY_ARRAY
    }
}
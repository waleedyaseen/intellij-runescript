package io.runescript.plugin.lang.psi.refs

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.*
import com.intellij.util.ProcessingContext
import io.runescript.plugin.lang.psi.named.RsNamedElement

class RsReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns.not(PlatformPatterns.alwaysFalse()),
            RsReferenceProvider()
        )
    }
}

class RsReferenceProvider : PsiReferenceProvider() {

    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        if (element is RsNamedElement) {
            val namedIdentifier = element.nameIdentifier!!
            return arrayOf(RsReference(element, namedIdentifier.textRangeInParent))
        }
        return PsiReference.EMPTY_ARRAY
    }
}
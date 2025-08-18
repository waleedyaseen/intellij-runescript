package io.runescript.plugin.ide.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionInitializationContext
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.completion.CompletionUtil
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.patterns.PsiElementPattern
import com.intellij.psi.PsiElement
import io.runescript.plugin.lang.RuneScript
import io.runescript.plugin.lang.psi.RsArgumentList
import io.runescript.plugin.lang.psi.RsTokenTypesSets

class RsCompletionContributor : CompletionContributor() {

    init {
        extend(
            CompletionType.BASIC,
            base().and(notCommentOrString()),
            RsCompletionProviderKeywords()
        )
        extend(
            CompletionType.BASIC,
            base().and(notCommentOrString()),
            RsCompletionProviderVariables()
        )
        extend(
            CompletionType.BASIC,
            base().and(notCommentOrString()),
            RsCompletionProviderCommand()
        )
        extend(
            CompletionType.BASIC,
            base().and(notCommentOrString()),
            RsCompletionProviderProc()
        )
    }

    override fun beforeCompletion(context: CompletionInitializationContext) {
        context.dummyIdentifier = computeDummyIdentifier(context)
    }

    private fun computeDummyIdentifier(context: CompletionInitializationContext): String {
        val element = context.file.findElementAt(context.startOffset)
        val parent = element?.parent
        if (parent is RsArgumentList) {
            return CompletionUtil.DUMMY_IDENTIFIER_TRIMMED
        } else {
            return "${CompletionUtil.DUMMY_IDENTIFIER};"
        }
    }

    private fun base(): PsiElementPattern.Capture<PsiElement> {
        return psiElement()
            .withLanguage(RuneScript)
    }

    private fun notCommentOrString(): PsiElementPattern.Capture<PsiElement> {
        return psiElement()
            .andNot(psiElement().withElementType(RsTokenTypesSets.COMMENTS))
            .andNot(psiElement().withElementType(RsTokenTypesSets.STRING_ELEMENTS))
    }
}

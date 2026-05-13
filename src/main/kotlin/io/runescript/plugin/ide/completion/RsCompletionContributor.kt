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
            RsCompletionProvider(),
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
        }
        if (hasIdentifierPrefix(context) && hasArgumentListAfter(context)) {
            return CompletionUtil.DUMMY_IDENTIFIER_TRIMMED
        }
        if (hasIdentifierPrefix(context)) {
            return ""
        }
        return "${CompletionUtil.DUMMY_IDENTIFIER};"
    }

    private fun hasIdentifierPrefix(context: CompletionInitializationContext): Boolean {
        val offset = context.startOffset
        if (offset <= 0) {
            return false
        }
        val previous = context.editor.document.charsSequence[offset - 1]
        return previous.isLetterOrDigit() || previous == '_' || previous == '.' || previous == ':'
    }

    private fun hasArgumentListAfter(context: CompletionInitializationContext): Boolean {
        val text = context.editor.document.charsSequence
        var offset = context.startOffset
        while (offset < text.length && text[offset].isWhitespace()) {
            offset++
        }
        return offset < text.length && text[offset] == '('
    }

    private fun base(): PsiElementPattern.Capture<PsiElement> =
        psiElement()
            .withLanguage(RuneScript)

    private fun notCommentOrString(): PsiElementPattern.Capture<PsiElement> =
        psiElement()
            .andNot(psiElement().withElementType(RsTokenTypesSets.COMMENTS))
            .andNot(psiElement().withElementType(RsTokenTypesSets.STRING_ELEMENTS))
}

package io.runescript.plugin.ide.injection

import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import io.runescript.plugin.lang.RuneScript
import io.runescript.plugin.lang.psi.RsStringLiteralContent
import io.runescript.plugin.lang.psi.isHookExpression

class RsHookLanguageInjector : MultiHostInjector {
    override fun elementsToInjectIn(): List<Class<out PsiElement>> = listOf(RsStringLiteralContent::class.java)

    override fun getLanguagesToInject(
        registrar: MultiHostRegistrar,
        context: PsiElement,
    ) {
        val host = context as? RsStringLiteralContent ?: return
        if (!host.isValidHost || !host.isHookExpression()) return

        registrar
            .startInjecting(RuneScript)
            .addPlace(null, null, host, TextRange.from(0, host.textLength))
            .doneInjecting()
    }
}

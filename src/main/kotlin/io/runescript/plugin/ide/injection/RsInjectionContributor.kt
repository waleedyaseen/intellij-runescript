package io.runescript.plugin.ide.injection

import com.intellij.lang.injection.general.Injection
import com.intellij.lang.injection.general.LanguageInjectionContributor
import com.intellij.lang.injection.general.SimpleInjection
import com.intellij.psi.PsiElement
import io.runescript.plugin.lang.RuneScript
import io.runescript.plugin.lang.psi.RsStringLiteralContent
import io.runescript.plugin.lang.psi.isHookExpression

class RsInjectionContributor : LanguageInjectionContributor {
    override fun getInjection(context: PsiElement): Injection? {
        if (context !is RsStringLiteralContent || !context.isHookExpression()) {
            return null
        }
        return SimpleInjection(RuneScript, "", "", null)
    }
}
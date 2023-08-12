package io.runescript.plugin.ide.spellchecker

import com.intellij.psi.PsiElement
import com.intellij.spellchecker.tokenizer.SpellcheckingStrategy
import io.runescript.plugin.lang.RuneScript

class RsSpellcheckingStrategy : SpellcheckingStrategy() {

    override fun isMyContext(element: PsiElement) = RuneScript.`is`(element.language)
}
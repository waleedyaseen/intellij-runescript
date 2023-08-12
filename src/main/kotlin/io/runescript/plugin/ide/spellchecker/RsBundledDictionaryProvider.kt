package io.runescript.plugin.ide.spellchecker

import com.intellij.spellchecker.BundledDictionaryProvider

class RsBundledDictionaryProvider : BundledDictionaryProvider {
    override fun getBundledDictionaries() = arrayOf("runescript.dic")
}
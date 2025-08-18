package io.runescript.plugin.ide.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.ProcessingContext
import io.runescript.plugin.lang.psi.type.RsPrimitiveType

class RsCompletionProviderKeywords : RsCompletionProviderBase() {
    private val keywords = listOf(
        "if",
        "else",
        "while",
        "switch",
        "case",
        "default",
        "return",
        "true",
        "false",
        "null",
        "calc",
        *RsPrimitiveType.entries.filter { it.isDeclarable }.map { it.literal }.toTypedArray(),
        *RsPrimitiveType.entries.filter { it.isDeclarable }.map { "def_${it.literal}" }.toTypedArray(),
        *RsPrimitiveType.entries.filter { it.isDeclarable }.map { "${it.literal}array" }.toTypedArray(),
    ).map {
        LookupElementBuilder
            .create(it)
            .withBoldness(true)
    }

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        for (element in keywords) {
            result.addElement(element)
        }
    }
}
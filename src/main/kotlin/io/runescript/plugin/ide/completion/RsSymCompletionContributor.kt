package io.runescript.plugin.ide.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.module.ModuleUtil
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.scriptName
import io.runescript.plugin.lang.stubs.index.RsClientScriptIndex
import io.runescript.plugin.symbollang.psi.RsSymField
import io.runescript.plugin.symbollang.psi.RsSymSymbol
import io.runescript.plugin.symbollang.psi.resolveToSymTypeName

class RsSymCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(),
            RsSymClientscriptCompletionProvider(),
        )
    }
}

private class RsSymClientscriptCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet,
    ) {
        val field = parameters.position.parentOfType<RsSymField>(withSelf = true) ?: return
        val symbol = field.parent as? RsSymSymbol ?: return
        if (symbol.fieldList.getOrNull(1) !== field) return
        if (resolveToSymTypeName(field.containingFile) != "clientscript") return
        if (!field.text.startsWith("[clientscript,")) return

        val module = ModuleUtil.findModuleForPsiElement(field) ?: return
        val project = field.project
        val scope = GlobalSearchScope.moduleScope(module)
        val prefix =
            field.text
                .substringAfterLast(',')
                .substringBefore(COMPLETION_DUMMY_IDENTIFIER)
                .trim()
        val prefixedResult = result.withPrefixMatcher(prefix)
        val cache = RsCompletionIndexCache.get(project)
        for (key in cache.matchingScriptKeys(RsClientScriptIndex.KEY, prefix)) {
            val scripts =
                StubIndex.getElements(
                    RsClientScriptIndex.KEY,
                    key,
                    project,
                    scope,
                    RsScript::class.java,
                )
            for (script in scripts) {
                prefixedResult.addElement(
                    LookupElementBuilder
                        .create(script, script.scriptName)
                        .withTypeText("clientscript"),
                )
            }
        }
    }

    private companion object {
        const val COMPLETION_DUMMY_IDENTIFIER = "IntellijIdeaRulezzz"
    }
}

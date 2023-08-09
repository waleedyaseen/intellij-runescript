package io.runescript.plugin.ide

import com.intellij.navigation.ChooseByNameContributorEx
import com.intellij.navigation.GotoClassContributor
import com.intellij.navigation.NavigationItem
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.util.Processor
import com.intellij.util.indexing.FindSymbolParameters
import com.intellij.util.indexing.IdFilter
import io.runescript.plugin.lang.RuneScript
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.qualifiedName
import io.runescript.plugin.lang.stubs.index.RsGotoScriptIndex

class RsGotoClassContributor : ChooseByNameContributorEx, GotoClassContributor {
    override fun processNames(processor: Processor<in String>, scope: GlobalSearchScope, filter: IdFilter?) {
        StubIndex.getInstance().processAllKeys(
                RsGotoScriptIndex.KEY,
                processor,
                scope,
                null
        )
    }

    override fun processElementsWithName(name: String, processor: Processor<in NavigationItem>, parameters: FindSymbolParameters) {
        val originScope = parameters.searchScope
        StubIndex.getInstance().processElements(
                RsGotoScriptIndex.KEY,
                name,
                parameters.project,
                originScope,
                null,
                RsScript::class.java
        ) { element ->
            processor.process(element)
        }
    }

    override fun getQualifiedName(item: NavigationItem): String? {
        return (item as? RsScript)?.qualifiedName
    }

    override fun getQualifiedNameSeparator(): String? {
        return "|"
    }

    // These are only effective if we override the IdeLanguageCustomization

    override fun getElementKind(): String {
        return RsBundle.message("go.to.script.kind.text")
    }

    override fun getElementKindsPluralized(): List<String> {
        return listOf(RsBundle.message("go.to.script.kind.text.pluralized"))
    }

    override fun getElementLanguage() = RuneScript
}
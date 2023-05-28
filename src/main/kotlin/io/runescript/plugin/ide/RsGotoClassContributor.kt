package io.runescript.plugin.ide

import com.intellij.navigation.ChooseByNameContributorEx
import com.intellij.navigation.GotoClassContributor
import com.intellij.navigation.NavigationItem
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.util.Processor
import com.intellij.util.indexing.FindSymbolParameters
import com.intellij.util.indexing.IdFilter
import io.runescript.plugin.lang.psi.RsScriptHeader
import io.runescript.plugin.lang.psi.RsScriptName
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
                RsScriptHeader::class.java
        ) { element ->
            processor.process(element.scriptName)
        }
    }

    override fun getQualifiedName(item: NavigationItem?): String? {
        val scriptName = item as? RsScriptName
        if (scriptName != null) {
            return "${scriptName.triggerExpression.text}|${scriptName.nameExpression!!.text}"
        }
        return null
    }

    override fun getQualifiedNameSeparator(): String? {
        return "|";
    }
}
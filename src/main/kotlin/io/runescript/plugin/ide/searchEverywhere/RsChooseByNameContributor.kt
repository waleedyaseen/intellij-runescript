package io.runescript.plugin.ide.searchEverywhere

import com.intellij.navigation.ChooseByNameContributorEx
import com.intellij.navigation.NavigationItem
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.util.Processor
import com.intellij.util.indexing.FindSymbolParameters
import com.intellij.util.indexing.IdFilter
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.stubs.index.RsClientScriptIndex
import io.runescript.plugin.lang.stubs.index.RsCommandScriptIndex
import io.runescript.plugin.lang.stubs.index.RsProcScriptIndex

class RsChooseByNameContributor : ChooseByNameContributorEx {

    private val keys = arrayOf(
        RsProcScriptIndex.KEY,
        RsClientScriptIndex.KEY,
        RsCommandScriptIndex.KEY
    )

    override fun processNames(processor: Processor<in String>, scope: GlobalSearchScope, filter: IdFilter?) {
        for (key in keys) {
            StubIndex.getInstance().processAllKeys(
                key,
                processor,
                scope,
                null
            )
        }
    }

    override fun processElementsWithName(
        name: String,
        processor: Processor<in NavigationItem>,
        parameters: FindSymbolParameters
    ) {
        val originScope = parameters.searchScope
        for (key in keys) {
            StubIndex.getInstance().processElements(
                key,
                name,
                parameters.project,
                originScope,
                null,
                RsScript::class.java
            ) { element ->
                processor.process(element)
            }
        }
    }
}
package io.runescript.plugin.ide.searchEverywhere

import com.intellij.navigation.ChooseByNameContributorEx
import com.intellij.navigation.NavigationItem
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.util.Processor
import com.intellij.util.indexing.FindSymbolParameters
import com.intellij.util.indexing.IdFilter
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.stubs.index.RsScriptNameIndex

class RsChooseByNameContributor : ChooseByNameContributorEx {
    override fun processNames(
        processor: Processor<in String>,
        scope: GlobalSearchScope,
        filter: IdFilter?,
    ) {
        StubIndex.getInstance().processAllKeys(
            RsScriptNameIndex.KEY,
            processor,
            scope,
            null,
        )
    }

    override fun processElementsWithName(
        name: String,
        processor: Processor<in NavigationItem>,
        parameters: FindSymbolParameters,
    ) {
        val originScope = parameters.searchScope
        StubIndex.getInstance().processElements(
            RsScriptNameIndex.KEY,
            name,
            parameters.project,
            originScope,
            null,
            RsScript::class.java,
            processor,
        )
    }
}

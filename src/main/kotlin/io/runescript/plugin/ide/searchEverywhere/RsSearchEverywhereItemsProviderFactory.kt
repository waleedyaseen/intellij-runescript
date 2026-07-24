package io.runescript.plugin.ide.searchEverywhere

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.application.EDT
import com.intellij.openapi.application.smartReadAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.searchEverywhere.SeItem
import com.intellij.platform.searchEverywhere.SeItemsProvider
import com.intellij.platform.searchEverywhere.SeItemsProviderFactory
import com.intellij.platform.searchEverywhere.SeParams
import com.intellij.platform.searchEverywhere.presentations.SeItemPresentation
import com.intellij.platform.searchEverywhere.presentations.SeTargetItemPresentationBuilder
import com.intellij.pom.Navigatable
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.MinusculeMatcher
import com.intellij.psi.codeStyle.NameUtil
import com.intellij.util.OpenSourceUtil
import com.intellij.util.text.matching.MatchingMode
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.qualifiedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.max

class RsSearchEverywhereItemsProviderFactory : SeItemsProviderFactory {
    override val id: String = RsSearchEverywhereContributor.ID

    override suspend fun getItemsProvider(
        project: Project?,
        dataContext: DataContext,
    ): SeItemsProvider? = project?.let(::RsSearchEverywhereItemsProvider)
}

private class RsSearchEverywhereItemsProvider(
    private val project: Project,
) : SeItemsProvider {
    override val id: String = RsSearchEverywhereContributor.ID
    override val displayName: String = "Scripts"

    override suspend fun collectItems(
        params: SeParams,
        collector: SeItemsProvider.Collector,
    ) {
        for (item in findItems(params.inputQuery)) {
            if (!collector.put(item)) {
                break
            }
        }
    }

    override suspend fun itemSelected(
        item: SeItem,
        modifiers: Int,
        searchText: String,
    ): Boolean {
        val script = (item as? RsSearchEverywhereItem)?.script ?: return false
        withContext(Dispatchers.EDT) {
            OpenSourceUtil.navigate(true, script)
        }
        return true
    }

    override suspend fun canBeShownInFindResults(): Boolean = true

    override fun getPsiElementForItem(item: SeItem): PsiElement? = (item as? RsSearchEverywhereItem)?.script

    override fun getVirtualFileForItem(item: SeItem): VirtualFile? = (item as? RsSearchEverywhereItem)?.script?.containingFile?.virtualFile

    override fun getNavigatableForItem(item: SeItem): Navigatable? = (item as? RsSearchEverywhereItem)?.script

    override fun dispose() {
    }

    private suspend fun findItems(searchText: String): List<RsSearchEverywhereItem> =
        smartReadAction(project) {
            val model = RsGotoScriptModel(project)
            model.setFilterItems(selectedTriggers())

            val query = searchText.trim()
            val nameQuery = scriptNameQuery(query)
            val nameMatcher = createMatcher(nameQuery)
            val qualifiedNameMatcher = createMatcher(query)

            model
                .getNames(false)
                .asSequence()
                .filter { nameQuery.isEmpty() || nameMatcher.matches(it) }
                .flatMap { name ->
                    model
                        .getElementsByName(name, false, query)
                        .asSequence()
                        .filterIsInstance<RsScript>()
                }.mapNotNull { script ->
                    val qualifiedName = script.qualifiedName
                    if (query.isNotEmpty() &&
                        !qualifiedNameMatcher.matches(qualifiedName) &&
                        !nameMatcher.matches(script.name.orEmpty())
                    ) {
                        return@mapNotNull null
                    }
                    val presentation = script.presentation
                    val presentableText = presentation?.presentableText ?: qualifiedName
                    RsSearchEverywhereItem(
                        script = script,
                        presentableText = presentableText,
                        locationText = presentation?.locationString ?: script.containingFile.name,
                        icon = presentation?.getIcon(false),
                        matchedRanges = createMatcher(query).match(presentableText).orEmpty(),
                        searchWeight =
                            if (query.isEmpty()) {
                                0
                            } else {
                                max(
                                    qualifiedNameMatcher.matchingDegree(qualifiedName),
                                    nameMatcher.matchingDegree(script.name.orEmpty()),
                                )
                            },
                    )
                }.sortedByDescending(RsSearchEverywhereItem::weight)
                .toList()
        }

    private fun selectedTriggers(): List<RsTriggerRef> {
        val configuration = RsGotoScriptSymbolConfiguration.getInstance(project)
        return RsTriggerRef.forAllTriggers().filter(configuration::isVisible)
    }

    private fun scriptNameQuery(query: String): String {
        val unqualifiedQuery = query.substringAfterLast(',').trimEnd(']')
        return if (unqualifiedQuery == query && query.removePrefix("[") in TRIGGER_NAMES) {
            ""
        } else {
            unqualifiedQuery
        }
    }

    private fun createMatcher(query: String): MinusculeMatcher =
        NameUtil
            .buildMatcher("*$query")
            .withMatchingMode(MatchingMode.IGNORE_CASE)
            .withSeparators("[],-_ ")
            .preferringStartMatches()
            .build()

    private companion object {
        val TRIGGER_NAMES = RsTriggerRef.forAllTriggers().mapTo(hashSetOf(), RsTriggerRef::displayName)
    }
}

private class RsSearchEverywhereItem(
    val script: RsScript,
    private val presentableText: String,
    private val locationText: String,
    private val icon: javax.swing.Icon?,
    private val matchedRanges: List<com.intellij.util.text.matching.MatchedFragment>,
    private val searchWeight: Int,
) : SeItem {
    override val rawObject: Any
        get() = script

    override fun weight(): Int = searchWeight

    override suspend fun presentation(): SeItemPresentation =
        SeTargetItemPresentationBuilder()
            .withIcon(icon)
            .withPresentableText(presentableText)
            .withPresentableTextMatchedRanges(matchedRanges)
            .withLocationText(locationText)
            .build()
}

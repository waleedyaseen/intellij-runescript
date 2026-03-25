package io.runescript.plugin.ide.searchEverywhere

import com.intellij.ide.actions.searcheverywhere.AbstractGotoSEContributor
import com.intellij.ide.actions.searcheverywhere.ExtendedInfo
import com.intellij.ide.actions.searcheverywhere.PSIPresentationBgRendererWrapper
import com.intellij.ide.actions.searcheverywhere.PersistentSearchEverywhereContributorFilter
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributorFactory
import com.intellij.ide.actions.searcheverywhere.SearchFieldActionsContributor
import com.intellij.ide.actions.searcheverywhere.footer.createPsiExtendedInfo
import com.intellij.ide.util.gotoByName.FilteringGotoByModel
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.util.containers.ContainerUtil

class RsSearchEverywhereContributor(
    event: AnActionEvent,
) : AbstractGotoSEContributor(event),
    SearchFieldActionsContributor {
    private val filter = createTriggerFilter(event.getData(CommonDataKeys.PROJECT)!!)

    override fun getGroupName(): String = "Scripts"

    override fun getFullGroupName(): String = "Scripts"

    override fun getSortWeight(): Int = 400

    override fun createModel(project: Project): FilteringGotoByModel<RsTriggerRef> {
        val model = RsGotoScriptModel(project)
        model.setFilterItems(filter.selectedElements)
        return model
    }

    override fun getActions(onChanged: Runnable): List<AnAction> = doGetActions(filter, null, onChanged)

    override fun isEmptyPatternSupported(): Boolean = true

    @Suppress("OVERRIDE_DEPRECATION")
    override fun getElementPriority(
        element: Any,
        searchPattern: String,
    ): Int = super.getElementPriority(element, searchPattern) + 5

    override fun createExtendedInfo(): ExtendedInfo = createPsiExtendedInfo(psiElement = { null })

    override fun createRightActions(
        registerShortcut: (AnAction) -> Unit,
        onChanged: Runnable,
    ): List<AnAction> = ContainerUtil.emptyList()

    class Factory : SearchEverywhereContributorFactory<Any> {
        override fun createContributor(initEvent: AnActionEvent): SearchEverywhereContributor<Any> =
            PSIPresentationBgRendererWrapper.wrapIfNecessary(RsSearchEverywhereContributor(initEvent))

        override fun isAvailable(project: Project): Boolean = true
    }

    companion object {
        fun createTriggerFilter(project: Project): PersistentSearchEverywhereContributorFilter<RsTriggerRef> {
            val items = RsTriggerRef.forAllTriggers()
            val persistentConfig = RsGotoScriptSymbolConfiguration.getInstance(project)
            return PersistentSearchEverywhereContributorFilter(
                items,
                persistentConfig,
                RsTriggerRef::displayName,
                RsTriggerRef::icon,
            )
        }
    }
}

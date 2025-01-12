package io.runescript.plugin.ide.searchEverywhere

import com.intellij.ide.actions.searcheverywhere.*
import com.intellij.ide.actions.searcheverywhere.footer.createPsiExtendedInfo
import com.intellij.ide.util.gotoByName.FilteringGotoByModel
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.util.containers.ContainerUtil

class RsSearchEverywhereContributor(event: AnActionEvent) : AbstractGotoSEContributor(event),
    SearchFieldActionsContributor {

    private val filter = createTriggerFilter(event.getRequiredData(CommonDataKeys.PROJECT))

    override fun getGroupName(): String {
        return "Scripts"
    }

    override fun getFullGroupName(): String {
        return "Scripts"
    }

    override fun getSortWeight(): Int {
        return 400
    }

    override fun createModel(project: Project): FilteringGotoByModel<RsTriggerRef> {
        val model = RsGotoScriptModel(project)
        model.setFilterItems(filter.selectedElements)
        return model
    }

    override fun getActions(onChanged: Runnable): List<AnAction> {
        return doGetActions(filter, null, onChanged)
    }

    override fun isEmptyPatternSupported(): Boolean {
        return true
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun getElementPriority(element: Any, searchPattern: String): Int {
        return super.getElementPriority(element, searchPattern) + 5
    }

    override fun createExtendedInfo(): ExtendedInfo {
        return createPsiExtendedInfo(psiElement = { null })
    }

    override fun createRightActions(
        registerShortcut: (AnAction) -> Unit,
        onChanged: Runnable
    ): List<AnAction> {
        return ContainerUtil.emptyList()
    }

    class Factory : SearchEverywhereContributorFactory<Any> {
        override fun createContributor(initEvent: AnActionEvent): SearchEverywhereContributor<Any> {
            return PSIPresentationBgRendererWrapper.wrapIfNecessary(RsSearchEverywhereContributor(initEvent))
        }

        override fun isAvailable(project: Project): Boolean {
            return true
        }
    }

    companion object {
        fun createTriggerFilter(project: Project): PersistentSearchEverywhereContributorFilter<RsTriggerRef> {
            val items = RsTriggerRef.forAllTriggers()
            val persistentConfig = RsGotoScriptSymbolConfiguration.getInstance(project)
            return PersistentSearchEverywhereContributorFilter(
                items,
                persistentConfig,
                RsTriggerRef::displayName,
                RsTriggerRef::icon
            )
        }
    }
}

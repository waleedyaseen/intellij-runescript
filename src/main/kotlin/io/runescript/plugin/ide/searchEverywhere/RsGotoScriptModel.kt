package io.runescript.plugin.ide.searchEverywhere

import com.intellij.ide.IdeBundle
import com.intellij.ide.util.gotoByName.FilteringGotoByModel
import com.intellij.navigation.ChooseByNameContributor
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.project.Project
import com.intellij.ui.IdeUICustomization
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.qualifiedName

class RsGotoScriptModel(project: Project) :
    FilteringGotoByModel<RsTriggerRef>(project, emptyArray()) {

    private val contributor = RsChooseByNameContributor()
    private val separators = emptyArray<String>()

    override fun getPromptText(): String {
        return "Enter script name"
    }

    override fun getNotInMessage(): String {
        return IdeUICustomization.getInstance().projectMessage("label.no.matches.found.in.project")
    }

    override fun getNotFoundMessage(): String {
        return IdeBundle.message("label.no.matches.found");
    }

    override fun getCheckBoxName(): String? {
        return null
    }

    override fun loadInitialCheckBoxState(): Boolean {
        return false
    }

    override fun saveInitialCheckBoxState(state: Boolean) {
    }

    override fun getSeparators(): Array<String> {
        return separators
    }

    override fun getFullName(item: Any): String? {
        return (item as? RsScript)?.qualifiedName
    }

    override fun willOpenEditor(): Boolean {
        return true
    }

    override fun filterValueFor(item: NavigationItem): RsTriggerRef? {
        return RsTriggerRef.forNavigationItem(item)
    }

    override fun getContributorList(): MutableList<ChooseByNameContributor> {
        return mutableListOf(contributor)
    }
}
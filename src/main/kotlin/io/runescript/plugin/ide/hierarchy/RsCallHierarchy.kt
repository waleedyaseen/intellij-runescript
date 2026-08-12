package io.runescript.plugin.ide.hierarchy

import com.intellij.ide.hierarchy.CallHierarchyBrowserBase
import com.intellij.ide.hierarchy.HierarchyBrowser
import com.intellij.ide.hierarchy.HierarchyNodeDescriptor
import com.intellij.ide.hierarchy.HierarchyProvider
import com.intellij.ide.hierarchy.HierarchyTreeStructure
import com.intellij.ide.util.treeView.NodeDescriptor
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ui.util.CompositeAppearance
import com.intellij.pom.Navigatable
import com.intellij.psi.PsiElement
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.lang.psi.RsCallExpression
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.qualifiedName
import io.runescript.plugin.lang.psi.scope.RsScopesUtil
import javax.swing.JTree

class RsCallHierarchyProvider : HierarchyProvider {
    override fun getTarget(dataContext: DataContext): PsiElement? {
        val direct = CommonDataKeys.PSI_ELEMENT.getData(dataContext)
        if (direct is RsScript) return direct
        if (direct != null) RsScopesUtil.parentScript(direct)?.let { return it }

        val file = CommonDataKeys.PSI_FILE.getData(dataContext) ?: return null
        val editor = CommonDataKeys.EDITOR.getData(dataContext) ?: return null
        return file.findElementAt(editor.caretModel.offset)?.let(RsScopesUtil::parentScript)
    }

    override fun createHierarchyBrowser(target: PsiElement): HierarchyBrowser = RsCallHierarchyBrowser(target.project, target as RsScript)

    override fun browserActivated(hierarchyBrowser: HierarchyBrowser) {
        (hierarchyBrowser as RsCallHierarchyBrowser).changeView(CallHierarchyBrowserBase.getCallerType())
    }
}

internal object RsCallHierarchyQueries {
    fun callers(script: RsScript): List<RsScript> =
        ReferencesSearch
            .search(script)
            .findAll()
            .mapNotNull { RsScopesUtil.parentScript(it.element) }
            .filter { it != script }
            .distinct()
            .sortedBy(RsScript::qualifiedName)

    fun callees(script: RsScript): List<RsScript> =
        PsiTreeUtil
            .findChildrenOfType(script, RsCallExpression::class.java)
            .mapNotNull { call -> call.reference?.resolve() as? RsScript }
            .filter { it != script }
            .distinct()
            .sortedBy(RsScript::qualifiedName)
}

private class RsCallHierarchyBrowser(
    project: Project,
    script: RsScript,
) : CallHierarchyBrowserBase(project, script) {
    override fun createTrees(type2TreeMap: MutableMap<in String, in JTree>) {
        type2TreeMap[getCallerType()] = createTree(false)
        type2TreeMap[getCalleeType()] = createTree(false)
    }

    override fun isApplicableElement(element: PsiElement): Boolean = element is RsScript

    override fun createHierarchyTreeStructure(
        typeName: String,
        psiElement: PsiElement,
    ): HierarchyTreeStructure =
        RsCallHierarchyTreeStructure(
            myProject,
            psiElement as RsScript,
            callers = typeName == getCallerType(),
        )

    override fun getComparator(): Comparator<NodeDescriptor<*>> = compareBy { it.toString() }

    override fun getElementFromDescriptor(descriptor: HierarchyNodeDescriptor): PsiElement? =
        (descriptor as? RsCallHierarchyNodeDescriptor)?.script
}

internal class RsCallHierarchyTreeStructure(
    project: Project,
    script: RsScript,
    private val callers: Boolean,
) : HierarchyTreeStructure(project, RsCallHierarchyNodeDescriptor(project, null, script, true)) {
    override fun buildChildren(descriptor: HierarchyNodeDescriptor): Array<Any> {
        val parent = descriptor as? RsCallHierarchyNodeDescriptor ?: return emptyArray()
        val scripts =
            if (callers) {
                RsCallHierarchyQueries.callers(parent.script)
            } else {
                RsCallHierarchyQueries.callees(parent.script)
            }
        return scripts
            .map { RsCallHierarchyNodeDescriptor(myProject, parent, it, false) }
            .toTypedArray()
    }
}

internal class RsCallHierarchyNodeDescriptor(
    project: Project,
    parent: NodeDescriptor<*>?,
    val script: RsScript,
    isBase: Boolean,
) : HierarchyNodeDescriptor(project, parent, script, isBase),
    Navigatable {
    override fun update(): Boolean {
        val oldText = myHighlightedText
        val oldIcon = icon
        val changed = super.update()
        if (!script.isValid) return invalidElement()

        installIcon(script, changed)
        myHighlightedText = CompositeAppearance()
        myHighlightedText.ending.addText(script.qualifiedName, textAttributesFor(script))
        val fileName = script.containingFile?.name
        if (fileName != null) {
            myHighlightedText.ending.addText("  $fileName", getPackageNameAttributes())
        }
        myName = script.qualifiedName
        return changed || oldText != myHighlightedText || oldIcon != icon
    }

    override fun isValid(): Boolean = script.isValid

    override fun navigate(requestFocus: Boolean) {
        (script as? Navigatable)?.navigate(requestFocus)
    }

    override fun canNavigate(): Boolean = (script as? Navigatable)?.canNavigate() == true

    override fun canNavigateToSource(): Boolean = (script as? Navigatable)?.canNavigateToSource() == true
}

package io.runescript.plugin.ide.breadcrumb

import com.intellij.openapi.util.NlsSafe
import com.intellij.psi.PsiElement
import com.intellij.ui.breadcrumbs.BreadcrumbsProvider
import io.runescript.plugin.lang.RuneScript
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.qualifiedName
import javax.swing.Icon

class RsBreadcrumbsInfoProvider : BreadcrumbsProvider {

    override fun getLanguages() = arrayOf(RuneScript)

    override fun acceptElement(e: PsiElement) = when (e) {
        is RsScript -> true
        else -> false
    }

    override fun getElementInfo(element: PsiElement): @NlsSafe String {
        if (element is RsScript) {
            return element.qualifiedName
        }
        return ""
    }

    override fun getElementIcon(element: PsiElement): Icon? {
        if (element is RsScript) {
            return element.getIcon(0)
        }
        return null
    }
}

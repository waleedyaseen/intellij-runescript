package io.runescript.plugin.ide.navigation

import com.intellij.ide.actions.QualifiedNameProvider
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.qualifiedName
import io.runescript.plugin.lang.stubs.index.RsScriptIndex
import io.runescript.plugin.symbollang.psi.RsSymSymbol
import io.runescript.plugin.symbollang.psi.index.RsSymbolIndex
import io.runescript.plugin.symbollang.psi.resolveToSymTypeName

class RsQualifiedNameProvider : QualifiedNameProvider {
    override fun adjustElementToCopy(element: PsiElement): PsiElement? {
        if (element is RsScript || element is RsSymSymbol) return element
        var current: PsiElement? = element
        while (current != null && current !is RsScript && current !is RsSymSymbol) {
            val resolved = current.reference?.resolve()
            if (resolved is RsScript || resolved is RsSymSymbol) return resolved
            current = current.parent
        }
        val declaration = current
        return declaration.takeIf { it is RsScript || it is RsSymSymbol }
    }

    override fun getQualifiedName(element: PsiElement): String? =
        when (element) {
            is RsScript -> element.qualifiedName
            is RsSymSymbol -> element.symbolQualifiedName()
            else -> null
        }

    override fun qualifiedNameToElement(
        fqn: String,
        project: Project,
    ): PsiElement? {
        val scope = GlobalSearchScope.projectScope(project)
        if (fqn.startsWith('[') && fqn.endsWith(']')) {
            return StubIndex
                .getElements(RsScriptIndex.KEY, fqn, project, scope, RsScript::class.java)
                .singleOrNull()
        }
        val type = fqn.substringBefore(':', missingDelimiterValue = "")
        val name = fqn.substringAfter(':', missingDelimiterValue = "")
        if (type.isEmpty() || name.isEmpty()) return null
        return StubIndex
            .getElements(RsSymbolIndex.KEY, name, project, scope, RsSymSymbol::class.java)
            .singleOrNull { resolveToSymTypeName(it.containingFile) == type }
    }

    private fun RsSymSymbol.symbolQualifiedName(): String? {
        val type = resolveToSymTypeName(containingFile) ?: return null
        return "$type:${name ?: return null}"
    }
}

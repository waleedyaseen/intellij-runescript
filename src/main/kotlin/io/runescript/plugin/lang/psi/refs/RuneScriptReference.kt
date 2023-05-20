package io.runescript.plugin.lang.psi.refs

import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.childrenOfType
import com.intellij.psi.util.findParentOfType
import com.intellij.psi.util.parentsOfType
import io.runescript.plugin.lang.psi.RuneScriptBlockStatement
import io.runescript.plugin.lang.psi.RuneScriptLocalVariableDeclarationStatement
import io.runescript.plugin.lang.psi.RuneScriptLocalVariableExpression
import io.runescript.plugin.lang.psi.RuneScriptPsiImplUtil
import io.runescript.plugin.lang.psi.RuneScriptScript
import io.runescript.plugin.lang.psi.RuneScriptStatementList

class RuneScriptReference(element: PsiElement, textRange: TextRange) :
        PsiReferenceBase<PsiElement>(element, textRange), PsiPolyVariantReference {

    override fun resolve(): PsiElement? {
        val result = multiResolve(false);
        return if (result.isEmpty()) null else result[0].element
    }

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val parentBlocks = findParentScopeBlocks(element)
        val targetName = (element as RuneScriptLocalVariableExpression).name
        return parentBlocks
                .flatMap { it.childrenOfType<RuneScriptLocalVariableDeclarationStatement>() + it.childrenOfType<RuneScriptLocalVariableExpression>() }
                .map {
                    if (it is RuneScriptLocalVariableDeclarationStatement) {
                        it.nameExpression
                    } else {
                        it
                    } as RuneScriptLocalVariableExpression
                }
                .distinct()
                .filter { it.name == targetName }
                .map { PsiElementResolveResult(it) }
                .toList()
                .toTypedArray()
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        return (element as RuneScriptLocalVariableExpression).setName(newElementName)
    }

    private fun findParentScopeBlocks(element: PsiElement): Sequence<PsiElement> {
        return element.parentsOfType(RuneScriptStatementList::class.java) + element.parentsOfType(RuneScriptBlockStatement::class.java)
    }
}
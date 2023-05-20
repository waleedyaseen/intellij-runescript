package io.runescript.plugin.lang.psi.refs

import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.util.childrenOfType
import com.intellij.psi.util.parentsOfType
import io.runescript.plugin.lang.psi.RsBlockStatement
import io.runescript.plugin.lang.psi.RsLocalVariableDeclarationStatement
import io.runescript.plugin.lang.psi.RsLocalVariableExpression
import io.runescript.plugin.lang.psi.RsStatementList

class RsReference(element: PsiElement, textRange: TextRange) :
        PsiReferenceBase<PsiElement>(element, textRange), PsiPolyVariantReference {

    override fun resolve(): PsiElement? {
        val result = multiResolve(false);
        return if (result.isEmpty()) null else result[0].element
    }

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val parentBlocks = findParentScopeBlocks(element)
        val targetName = (element as RsLocalVariableExpression).name
        return parentBlocks
                .flatMap { it.childrenOfType<RsLocalVariableDeclarationStatement>() + it.childrenOfType<RsLocalVariableExpression>() }
                .map {
                    if (it is RsLocalVariableDeclarationStatement) {
                        it.nameExpression
                    } else {
                        it
                    } as RsLocalVariableExpression
                }
                .distinct()
                .filter { it.name == targetName }
                .map { PsiElementResolveResult(it) }
                .toList()
                .toTypedArray()
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        return (element as RsLocalVariableExpression).setName(newElementName)
    }

    private fun findParentScopeBlocks(element: PsiElement): Sequence<PsiElement> {
        return element.parentsOfType(RsStatementList::class.java) + element.parentsOfType(RsBlockStatement::class.java)
    }
}
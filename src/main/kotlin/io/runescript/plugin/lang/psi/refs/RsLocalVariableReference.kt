package io.runescript.plugin.lang.psi.refs

import com.intellij.psi.*
import com.intellij.psi.util.childrenOfType
import com.intellij.psi.util.findParentOfType
import com.intellij.psi.util.parentsOfType
import io.runescript.plugin.lang.psi.RsBlockStatement
import io.runescript.plugin.lang.psi.RsLocalVariableDeclarationStatement
import io.runescript.plugin.lang.psi.RsLocalVariableExpression
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.RsStatementList

class RsLocalVariableReference(element: RsLocalVariableExpression) :
        PsiReferenceBase<RsLocalVariableExpression>(element, element.nameLiteral.textRangeInParent), PsiPolyVariantReference {

    override fun resolve(): PsiElement? {
        val result = multiResolve(false);
        return if (result.isEmpty()) null else result[0].element
    }

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val parentBlocks = findParentScopeBlocks(element)
        val targetName = element.nameLiteral.text
        val localVariableExpressions = parentBlocks
                .flatMap { it.childrenOfType<RsLocalVariableDeclarationStatement>() + it.childrenOfType<RsLocalVariableExpression>() }
                .map {
                    if (it is RsLocalVariableDeclarationStatement) {
                        it.nameExpression
                    } else {
                        it
                    } as RsLocalVariableExpression
                }
                .distinct()
                .filter { it.variableName == targetName }
                .toMutableList()
        val script = element.findParentOfType<RsScript>()!!
        localVariableExpressions += script.scriptHeader.parameterList?.parameterList?.map { it.localVariableExpression } ?: emptyList()
        return localVariableExpressions
                .map { PsiElementResolveResult(it) }
                .toTypedArray()
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        element.variableName = newElementName
        return element
    }

    private fun findParentScopeBlocks(element: PsiElement): Sequence<PsiElement> {
        return element.parentsOfType(RsStatementList::class.java) + element.parentsOfType(RsBlockStatement::class.java)
    }
}
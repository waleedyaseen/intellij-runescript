package io.runescript.plugin.lang.psi.refs

import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.childrenOfType
import com.intellij.psi.util.findParentOfType
import io.runescript.plugin.lang.psi.RuneScriptLocalVariableDeclarationStatement
import io.runescript.plugin.lang.psi.RuneScriptLocalVariableExpression
import io.runescript.plugin.lang.psi.RuneScriptPsiImplUtil
import io.runescript.plugin.lang.psi.RuneScriptScript

class RuneScriptReference(element: PsiElement, textRange: TextRange) :
    PsiReferenceBase<PsiElement>(element, textRange), PsiPolyVariantReference {

    override fun resolve(): PsiElement? {
        val result = multiResolve(false);
        return result[0].element
    }

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val script = element.findParentOfType<RuneScriptScript>()!!
        val expressions = PsiTreeUtil.findChildrenOfAnyType(script, RuneScriptLocalVariableDeclarationStatement::class.java)
        return expressions
            .map { it.expressionList[0] as RuneScriptLocalVariableExpression }
            .filter { it.name == (element as RuneScriptLocalVariableExpression).name }
            .map { PsiElementResolveResult(it) }
            .toTypedArray()
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        return (element as RuneScriptLocalVariableExpression).setName(newElementName)
    }
}
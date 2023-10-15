package io.runescript.plugin.lang.psi.mixin

import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.search.SearchScope
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.parentOfType
import com.intellij.refactoring.suggested.startOffset
import io.runescript.plugin.ide.doc.findDoc
import io.runescript.plugin.lang.psi.*
import io.runescript.plugin.lang.psi.refs.RsLocalVariableReference
import io.runescript.plugin.lang.stubs.RsLocalVariableExpressionStub

abstract class RsLocalVariableExpressionMixin : StubBasedPsiElementBase<RsLocalVariableExpressionStub>, RsLocalVariableExpression {

    constructor(node: ASTNode) : super(node)
    constructor(stub: RsLocalVariableExpressionStub, type: IStubElementType<*, *>) : super(stub, type)
    constructor(stub: RsLocalVariableExpressionStub?, type: IElementType?, node: ASTNode?) : super(stub, type, node)

    override fun getUseScope(): SearchScope {
        val script = parentOfType<RsScript>()!!
        val doc = script.findDoc()
        return if (doc != null) {
            LocalSearchScope(arrayOf(script, doc))
        } else {
            LocalSearchScope(this)
        }
    }

    override fun getReference(): PsiReference? {
        if (isForVariableDeclaration() || isForArrayDeclaration()) {
            return null
        }
        return RsLocalVariableReference(this)
    }

    override fun getTextOffset(): Int {
        return nameLiteral.startOffset
    }

    override fun getNameIdentifier(): PsiElement {
        return nameLiteral
    }

    override fun getName(): String {
        return RsPsiImplUtil.getName(nameLiteral)
    }

    override fun setName(newName: String): PsiElement {
        return RsPsiImplUtil.setName(nameLiteral, newName)
    }
}
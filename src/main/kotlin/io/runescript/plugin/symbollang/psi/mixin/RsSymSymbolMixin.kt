package io.runescript.plugin.symbollang.psi.mixin

import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.SearchScope
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.tree.IElementType
import com.intellij.refactoring.suggested.startOffset
import io.runescript.plugin.symbollang.psi.RsSymPsiImplUtil
import io.runescript.plugin.symbollang.psi.RsSymSymbol
import io.runescript.plugin.symbollang.psi.stub.RsSymSymbolStub

abstract class RsSymSymbolMixin : StubBasedPsiElementBase<RsSymSymbolStub>, RsSymSymbol {

    constructor(node: ASTNode) : super(node)
    constructor(stub: RsSymSymbolStub, type: IStubElementType<*, *>) : super(stub, type)
    constructor(stub: RsSymSymbolStub?, type: IElementType?, node: ASTNode?) : super(stub, type, node)

    override fun getUseScope(): SearchScope {
        return GlobalSearchScope.projectScope(project)
    }

    override fun setName(name: String): PsiElement {
        return RsSymPsiImplUtil.setName(fieldList[1], name)
    }

    override fun getName(): String {
        return RsSymPsiImplUtil.getName(fieldList[1])
    }

    override fun getNameIdentifier(): PsiElement? {
        return fieldList[1]
    }

    override fun getTextOffset(): Int {
        return fieldList[1].startOffset
    }
}
package io.runescript.plugin.symbollang.psi.mixin

import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.lang.ASTNode
import com.intellij.openapi.module.ModuleUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.SearchScope
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.startOffset
import io.runescript.plugin.lang.psi.refs.RsSymClientscriptReference
import io.runescript.plugin.symbollang.psi.RsSymPsiImplUtil
import io.runescript.plugin.symbollang.psi.RsSymSymbol
import io.runescript.plugin.symbollang.psi.isConstantFile
import io.runescript.plugin.symbollang.psi.resolveToSymTypeName
import io.runescript.plugin.symbollang.psi.stub.RsSymSymbolStub

abstract class RsSymSymbolMixin : StubBasedPsiElementBase<RsSymSymbolStub>, RsSymSymbol {

    constructor(node: ASTNode) : super(node)
    constructor(stub: RsSymSymbolStub, type: IStubElementType<*, *>) : super(stub, type)
    constructor(stub: RsSymSymbolStub?, type: IElementType?, node: ASTNode?) : super(stub, type, node)

    override fun getUseScope(): SearchScope {
        val module = ModuleUtil.findModuleForPsiElement(this) ?: return super.getUseScope()
        return GlobalSearchScope.moduleScope(module)
    }

    override fun getReference(): PsiReference? {
        if (containingFile.virtualFile != null && resolveToSymTypeName(containingFile) == "clientscript") {
            val text = fieldList.getOrNull(1)?.text
            if (text != null && text.startsWith("[") && text.endsWith("]") && text.contains(",")) {
                return RsSymClientscriptReference(this)
            }
        }
        return super.getReference()
    }

    override fun setName(name: String) = RsSymPsiImplUtil.setName(fieldList[getNameFieldIndex()], name)

    override fun getName() = RsSymPsiImplUtil.getName(fieldList[getNameFieldIndex()])

    override fun getNameIdentifier(): PsiElement = fieldList[getNameFieldIndex()]

    override fun getTextOffset() = nameIdentifier.startOffset

    private fun getNameFieldIndex() = if (containingFile.isConstantFile()) 0 else 1
}
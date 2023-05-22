package io.runescript.plugin.lang.psi.mixin

import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.ide.projectView.PresentationData
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.tree.IElementType
import io.runescript.plugin.ide.RsIcons
import io.runescript.plugin.ide.highlight.RsSyntaxHighlighterColors
import io.runescript.plugin.lang.psi.RsScriptName
import io.runescript.plugin.lang.stubs.RsScriptNameStub

abstract class RsScriptNameMixin : StubBasedPsiElementBase<RsScriptNameStub>, RsScriptName {

    constructor(node: ASTNode) : super(node)
    constructor(stub: RsScriptNameStub, type: IStubElementType<*, *>) : super(stub, type)
    constructor(stub: RsScriptNameStub?, type: IElementType?, node: ASTNode?) : super(stub, type, node)

    override fun getPresentation(): ItemPresentation? {
        val icon = when (triggerExpression.text) {
            "proc" -> RsIcons.Proc
            "clientscript" -> RsIcons.Cs2
            else -> null
        }
        return PresentationData("[${triggerExpression.text},${nameExpression!!.text}]", containingFile.name, icon, RsSyntaxHighlighterColors.SCRIPT_DECLARATION)
    }
}
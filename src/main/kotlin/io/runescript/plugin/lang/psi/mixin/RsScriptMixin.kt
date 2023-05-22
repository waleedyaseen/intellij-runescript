package io.runescript.plugin.lang.psi.mixin

import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.ide.projectView.PresentationData
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.tree.IElementType
import io.runescript.plugin.ide.RsIcons
import io.runescript.plugin.ide.highlight.RsSyntaxHighlighterColors
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.RsScriptName
import io.runescript.plugin.lang.stubs.RsScriptNameStub
import io.runescript.plugin.lang.stubs.RsScriptStub

abstract class RsScriptMixin : StubBasedPsiElementBase<RsScriptStub>, RsScript {

    constructor(node: ASTNode) : super(node)
    constructor(stub: RsScriptStub, type: IStubElementType<*, *>) : super(stub, type)
    constructor(stub: RsScriptStub?, type: IElementType?, node: ASTNode?) : super(stub, type, node)

    override fun getPresentation(): ItemPresentation? {
        return scriptHeader.scriptName.presentation
    }
}
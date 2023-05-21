package io.runescript.plugin.lang.psi.mixin

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.ide.projectView.PresentationData
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiReference
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.tree.IElementType
import io.runescript.plugin.ide.RsIcons
import io.runescript.plugin.ide.highlight.RsSyntaxHighlighterColors
import io.runescript.plugin.lang.psi.RsGosubExpression
import io.runescript.plugin.lang.psi.RsScriptName
import io.runescript.plugin.lang.psi.refs.RsGosubReference
import io.runescript.plugin.lang.stubs.RsScriptNameStub

abstract class RsGosubExpressionMixin(node: ASTNode) : ASTWrapperPsiElement(node), RsGosubExpression {
    override fun getReference(): PsiReference? {
        return RsGosubReference(this)
    }
}
package io.runescript.plugin.ide.formatter.blocks.impl

import com.intellij.formatting.Indent
import com.intellij.lang.ASTNode
import io.runescript.plugin.ide.formatter.RsFormatterContext
import io.runescript.plugin.ide.formatter.blocks.RsBlock
import io.runescript.plugin.lang.psi.RsElementTypes.SCRIPT

class RsStatementListBlock(context: RsFormatterContext, node: ASTNode)
    : RsBlock(context, node, Indent.getNoneIndent(), null, null) {


    override fun getChildIndent(): Indent? {
        if (node.treeParent.elementType == SCRIPT) {
            return Indent.getNoneIndent()
        }
        return Indent.getNormalIndent()
    }
}
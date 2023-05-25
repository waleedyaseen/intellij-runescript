package io.runescript.plugin.ide.formatter.blocks.impl

import com.intellij.formatting.Indent
import com.intellij.lang.ASTNode
import io.runescript.plugin.ide.formatter.RsFormatterContext
import io.runescript.plugin.ide.formatter.blocks.RsBlock

class RsSwitchStatementBlock(context: RsFormatterContext, node: ASTNode)
    : RsBlock(context, node, Indent.getNoneIndent(), null, null) {

    override fun getChildIndent(): Indent? {
        return Indent.getNoneIndent()
    }

}
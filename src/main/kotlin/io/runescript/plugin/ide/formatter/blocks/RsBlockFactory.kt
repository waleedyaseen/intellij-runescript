package io.runescript.plugin.ide.formatter.blocks

import com.intellij.formatting.Indent
import com.intellij.lang.ASTNode
import io.runescript.plugin.ide.formatter.RsFormatterContext
import io.runescript.plugin.ide.formatter.blocks.impl.*
import io.runescript.plugin.lang.psi.RsElementTypes

object RsBlockFactory {
    fun create(context: RsFormatterContext, parent: RsBlock, node: ASTNode): RsBlock {
        val childIndent = parent.childIndent ?: Indent.getNoneIndent()
        return when (node.elementType) {
            RsElementTypes.SCRIPT -> RsScriptBlock(context, node)
            RsElementTypes.BLOCK_STATEMENT -> RsBracedBlock(context, node)
            RsElementTypes.STATEMENT_LIST -> RsStatementListBlock(context, node)
            RsElementTypes.SWITCH_STATEMENT -> RsSwitchStatementBlock(context, node, childIndent)
            RsElementTypes.SWITCH_CASE -> RsSwitchCaseBlock(context, node)
            else -> RsBlock(context, node, childIndent, null, null)
        }
    }
}

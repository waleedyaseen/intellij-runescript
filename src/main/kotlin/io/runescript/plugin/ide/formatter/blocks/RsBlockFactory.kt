package io.runescript.plugin.ide.formatter.blocks

import com.intellij.formatting.Indent
import com.intellij.lang.ASTNode
import io.runescript.plugin.ide.formatter.RsFormatterContext
import io.runescript.plugin.ide.formatter.blocks.impl.*
import io.runescript.plugin.lang.doc.lexer.RsDocTokens
import io.runescript.plugin.lang.psi.RsElementTypes

object RsBlockFactory {
    fun create(context: RsFormatterContext, parent: RsBlock, node: ASTNode): RsBlock {
        var childIndent = parent.childIndent ?: Indent.getNoneIndent()
        if (node.elementType == RsDocTokens.TAG_NAME) childIndent = Indent.getNoneIndent()
        if (node.elementType == RsDocTokens.LEADING_ASTERISK) childIndent = Indent.getSpaceIndent(1)
        if (node.elementType == RsDocTokens.START) childIndent = Indent.getSpaceIndent(0)
        if (node.elementType == RsDocTokens.END) childIndent = Indent.getSpaceIndent(1)
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

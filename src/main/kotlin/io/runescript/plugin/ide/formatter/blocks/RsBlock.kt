package io.runescript.plugin.ide.formatter.blocks

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.psi.TokenType
import com.intellij.psi.formatter.common.AbstractBlock
import io.runescript.plugin.ide.formatter.RsFormatterContext

open class RsBlock(private val context: RsFormatterContext, node: ASTNode, private val indent: Indent, alignment: Alignment?, wrap: Wrap?)
    : AbstractBlock(node, wrap, alignment) {

    public override fun getChildIndent(): Indent? {
        return super.getChildIndent()
    }

    override fun buildChildren(): MutableList<Block> {
        val blocks = mutableListOf<Block>()
        var child = myNode.firstChildNode
        while (child != null) {
            if (child.elementType != TokenType.WHITE_SPACE) {
                blocks.add(RsBlockFactory.create(context, this, child))
            }
            child = child.treeNext
        }
        return blocks
    }

    override fun getSpacing(child1: Block?, child2: Block): Spacing? {
        return context.spacingBuilder.getSpacing(this, child1, child2)
    }

    override fun getIndent() = indent

    override fun isLeaf() = myNode.firstChildNode == null
}

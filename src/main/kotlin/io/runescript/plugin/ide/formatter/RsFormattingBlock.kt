package io.runescript.plugin.ide.formatter

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.psi.TokenType
import com.intellij.psi.formatter.common.AbstractBlock


class RsFormattingBlock(node: ASTNode, wrap: Wrap?, alignment: Alignment, private val spacingBuilder: SpacingBuilder)
    : AbstractBlock(node, wrap, alignment) {

    override fun getIndent(): Indent = Indent.getNoneIndent()

    override fun getSpacing(child1: Block?, child2: Block): Spacing? {
        return spacingBuilder.getSpacing(this, child1, child2)
    }

    override fun isLeaf(): Boolean = myNode.firstChildNode == null

    override fun buildChildren(): MutableList<Block> {
        val blocks: MutableList<Block> = ArrayList()
        var child = myNode.firstChildNode
        while (child != null) {
            if (child.elementType != TokenType.WHITE_SPACE) {
                val block = RsFormattingBlock(child, Wrap.createWrap(WrapType.NONE, false), Alignment.createAlignment(), spacingBuilder)
                blocks.add(block)
            }
            child = child.treeNext
        }
        return blocks
    }
}
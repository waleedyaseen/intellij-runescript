package io.runescript.plugin.ide.folding

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.endOffset
import com.intellij.psi.util.startOffset
import io.runescript.plugin.lang.psi.*

class RsFoldingBuilder : FoldingBuilderEx(), DumbAware {
    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        val elements = PsiTreeUtil.findChildrenOfAnyType(
                root,
                RsBlockStatement::class.java,
                RsSwitchStatement::class.java,
                RsStatementList::class.java,
                PsiComment::class.java
        )
        val descriptors = ArrayList<FoldingDescriptor>(elements.size)
        elements.forEach { element ->
            when (element) {
                is RsBlockStatement, is RsStatementList -> {
                    if (!element.textRange.isEmpty) {
                        descriptors.add(FoldingDescriptor(element.node, element.textRange))
                    }
                }

                is RsSwitchStatement -> {
                    val lbrace = element.lbrace
                    val rbrace = element.rbrace
                    if (lbrace != null && rbrace != null) {
                        val startOffset = lbrace.startOffset
                        val endOffset = rbrace.endOffset
                        descriptors.add(FoldingDescriptor(element.node, TextRange.create(startOffset, endOffset)))
                    }
                }

                is PsiComment -> {
                    if (element.tokenType == RsTokenTypes.BLOCK_COMMENT && !element.textRange.isEmpty) {
                        descriptors.add(FoldingDescriptor(element.node, element.textRange))
                    }
                }
            }
        }
        return descriptors.toTypedArray()
    }

    override fun getPlaceholderText(node: ASTNode): String {
        when (node.elementType) {
            RsElementTypes.SWITCH_STATEMENT, RsElementTypes.BLOCK_STATEMENT, RsElementTypes.STATEMENT_LIST -> {
                return "{...}"
            }

            RsTokenTypes.BLOCK_COMMENT -> {
                val whiteSpace = node.text.indexOfFirst { !Character.isWhitespace(it) && it != '/' && it != '*' }
                val newLineIndex = node.text.indexOf('\n', whiteSpace).let { if (it == -1) node.text.length else it }
                if (whiteSpace == -1 || newLineIndex < 1) {
                    return "/.../\n"
                }
                return node.text.substring(whiteSpace, newLineIndex)
            }

            else -> return "...\n"
        }
    }

    override fun isCollapsedByDefault(node: ASTNode): Boolean {
        return false
    }
}
package io.runescript.plugin.ide.folding

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.lang.psi.RsBlockStatement
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.RsTokenTypes
import io.runescript.plugin.lang.psi.RsTypes

class RsFoldingBuilder : FoldingBuilderEx(), DumbAware {
    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        val elements = PsiTreeUtil.findChildrenOfAnyType(
            root,
            RsScript::class.java,
            RsBlockStatement::class.java,
            PsiComment::class.java
        )
        val descriptors = ArrayList<FoldingDescriptor>(elements.size)
        elements.forEach { element ->
            when (element) {
                is RsScript -> {
                    if (!element.statementList.textRange.isEmpty) {
                        descriptors.add(FoldingDescriptor(element.node, element.statementList.textRange))
                    }
                }

                is RsBlockStatement -> {
                    if (!element.textRange.isEmpty) {
                        descriptors.add(FoldingDescriptor(element.node, element.textRange))
                    }
                }

                is PsiComment -> {
                    if (element.tokenType == RsTokenTypes.MULTI_LINE_COMMENT && !element.textRange.isEmpty) {
                        descriptors.add(FoldingDescriptor(element.node, element.textRange))
                    }
                }
            }
        }
        return descriptors.toTypedArray()
    }

    override fun getPlaceholderText(node: ASTNode): String {
        when (node.elementType) {
            RsTypes.SCRIPT -> {
                return "{...}\n"
            }

            RsTypes.BLOCK_STATEMENT -> {
                return "{...}\n"
            }

            RsTokenTypes.MULTI_LINE_COMMENT -> {
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
package io.runescript.plugin.ide

import com.intellij.lang.CodeDocumentationAwareCommenter
import com.intellij.psi.PsiComment
import com.intellij.psi.tree.IElementType
import io.runescript.plugin.lang.doc.psi.api.RsDoc
import io.runescript.plugin.lang.psi.RsTokenTypes

class RsCommenter : CodeDocumentationAwareCommenter {

    override fun getLineCommentPrefix(): String {
        return "//"
    }

    override fun getBlockCommentPrefix(): String {
        return "/*"
    }

    override fun getBlockCommentSuffix(): String {
        return "*/"
    }

    override fun getCommentedBlockCommentPrefix(): String? {
        return null
    }

    override fun getCommentedBlockCommentSuffix(): String? {
        return null
    }

    override fun getLineCommentTokenType(): IElementType {
        return RsTokenTypes.LINE_COMMENT
    }

    override fun getBlockCommentTokenType(): IElementType {
        return RsTokenTypes.BLOCK_COMMENT
    }

    override fun getDocumentationCommentTokenType(): IElementType {
        return RsTokenTypes.DOC_COMMENT
    }

    override fun getDocumentationCommentPrefix(): String {
        return "/**"
    }

    override fun getDocumentationCommentLinePrefix(): String {
        return "*"
    }

    override fun getDocumentationCommentSuffix(): String {
        return "*/"
    }

    override fun isDocumentationComment(element: PsiComment?): Boolean {
        return element is RsDoc
    }
}
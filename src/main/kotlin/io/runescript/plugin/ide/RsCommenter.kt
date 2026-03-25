package io.runescript.plugin.ide

import com.intellij.lang.CodeDocumentationAwareCommenter
import com.intellij.psi.PsiComment
import com.intellij.psi.tree.IElementType
import io.runescript.plugin.lang.doc.psi.api.RsDoc
import io.runescript.plugin.lang.psi.RsTokenTypes

class RsCommenter : CodeDocumentationAwareCommenter {
    override fun getLineCommentPrefix(): String = "//"

    override fun getBlockCommentPrefix(): String = "/*"

    override fun getBlockCommentSuffix(): String = "*/"

    override fun getCommentedBlockCommentPrefix(): String? = null

    override fun getCommentedBlockCommentSuffix(): String? = null

    override fun getLineCommentTokenType(): IElementType = RsTokenTypes.LINE_COMMENT

    override fun getBlockCommentTokenType(): IElementType = RsTokenTypes.BLOCK_COMMENT

    override fun getDocumentationCommentTokenType(): IElementType = RsTokenTypes.DOC_COMMENT

    override fun getDocumentationCommentPrefix(): String = "/**"

    override fun getDocumentationCommentLinePrefix(): String = "*"

    override fun getDocumentationCommentSuffix(): String = "*/"

    override fun isDocumentationComment(element: PsiComment?): Boolean = element is RsDoc
}

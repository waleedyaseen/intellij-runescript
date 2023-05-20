package io.runescript.plugin.ide

import com.intellij.lang.CodeDocumentationAwareCommenter
import com.intellij.psi.PsiComment
import com.intellij.psi.tree.IElementType
import io.runescript.plugin.lang.psi.RuneScriptTokenTypes

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
        return RuneScriptTokenTypes.SINGLE_LINE_COMMENT
    }

    override fun getBlockCommentTokenType(): IElementType {
        return RuneScriptTokenTypes.MULTI_LINE_COMMENT
    }

    override fun getDocumentationCommentTokenType(): IElementType? {
        return null
    }

    override fun getDocumentationCommentPrefix(): String? {
        return null
    }

    override fun getDocumentationCommentLinePrefix(): String? {
        return null
    }

    override fun getDocumentationCommentSuffix(): String? {
        return null
    }

    override fun isDocumentationComment(element: PsiComment?): Boolean {
        return false
    }
}
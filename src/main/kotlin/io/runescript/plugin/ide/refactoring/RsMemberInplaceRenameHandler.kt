package io.runescript.plugin.ide.refactoring

import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.refactoring.rename.inplace.MemberInplaceRenameHandler
import io.runescript.plugin.lang.psi.RsScript

class RsMemberInplaceRenameHandler : MemberInplaceRenameHandler() {

    override fun isAvailable(element: PsiElement?, editor: Editor, file: PsiFile): Boolean {
        return element is RsScript
    }
}
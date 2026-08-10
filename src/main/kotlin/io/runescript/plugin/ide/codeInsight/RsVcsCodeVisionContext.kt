package io.runescript.plugin.ide.codeInsight

import com.intellij.codeInsight.hints.VcsCodeVisionCurlyBracketLanguageContext
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import io.runescript.plugin.lang.psi.RsElementTypes
import io.runescript.plugin.lang.psi.RsScript
import java.awt.event.MouseEvent

class RsVcsCodeVisionContext : VcsCodeVisionCurlyBracketLanguageContext() {
    override fun isAccepted(element: PsiElement): Boolean = element is RsScript

    override fun handleClick(
        mouseEvent: MouseEvent,
        editor: Editor,
        element: PsiElement,
    ) = Unit

    override fun isRBrace(element: PsiElement): Boolean = element.node.elementType == RsElementTypes.RBRACE
}

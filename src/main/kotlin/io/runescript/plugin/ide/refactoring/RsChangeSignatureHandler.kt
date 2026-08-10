package io.runescript.plugin.ide.refactoring

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.refactoring.changeSignature.ChangeSignatureHandler
import io.runescript.plugin.lang.psi.RsScript

class RsChangeSignatureHandler : ChangeSignatureHandler {
    override fun findTargetMember(element: PsiElement): PsiElement? =
        (element as? RsScript) ?: PsiTreeUtil.getParentOfType(element, RsScript::class.java, false)

    override fun invoke(
        project: Project,
        editor: Editor,
        file: PsiFile,
        dataContext: DataContext,
    ) {
        val script = findTargetMember(file, editor) as? RsScript ?: return
        RsChangeSignatureDialog(project, script).show()
    }

    override fun invoke(
        project: Project,
        elements: Array<out PsiElement>,
        dataContext: DataContext?,
    ) {
        val script = elements.firstNotNullOfOrNull { element -> findTargetMember(element) as? RsScript } ?: return
        RsChangeSignatureDialog(project, script).show()
    }

    override fun getTargetNotFoundMessage(): String = "Caret must be inside a RuneScript script."
}

package io.runescript.plugin.ide.refactoring

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsContexts
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.refactoring.rename.RenamePsiElementProcessor
import com.intellij.refactoring.rename.RenamePsiFileProcessor
import com.intellij.refactoring.rename.RenameRefactoringDialog
import com.intellij.util.containers.MultiMap
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.lang.psi.RsLocalVariableExpression
import io.runescript.plugin.lang.psi.scope.RsLocalVariableResolver
import io.runescript.plugin.lang.psi.scope.RsResolveMode
import io.runescript.plugin.lang.psi.scope.RsScopesUtil

class RsRenameProcessor : RenamePsiElementProcessor() {

    override fun createDialog(
        project: Project, element: PsiElement, nameSuggestionContext: PsiElement?, editor: Editor?
    ): RenameRefactoringDialog? {
        return RenamePsiFileProcessor.PsiFileRenameDialog(project, element, nameSuggestionContext, editor)
    }

    override fun findExistingNameConflicts(
        element: PsiElement, newName: String, conflicts: MultiMap<PsiElement?, @NlsContexts.DialogMessage String?>
    ) {
        val resolver = RsLocalVariableResolver(newName, RsResolveMode.Both)
        RsScopesUtil.walkUpScopes(resolver, ResolveState.initial(), element)
        if (resolver.declaration != null) {
            conflicts.putValue(
                resolver.declaration, RsBundle.message("refactoring.error.duplicate.local.variable", newName)
            )
        }
    }

    override fun canProcessElement(element: PsiElement): Boolean {
        return element is RsLocalVariableExpression
    }
}
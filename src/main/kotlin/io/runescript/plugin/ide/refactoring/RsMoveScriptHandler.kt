package io.runescript.plugin.ide.refactoring

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.messages.MessagesService
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiParserFacade
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.refactoring.move.MoveCallback
import com.intellij.refactoring.move.MoveHandlerDelegate
import io.runescript.plugin.ide.filetypes.RsFileType
import io.runescript.plugin.lang.psi.RsFile
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.qualifiedName

class RsMoveScriptHandler : MoveHandlerDelegate() {
    override fun canMove(
        elements: Array<out PsiElement>,
        targetContainer: PsiElement?,
        reference: com.intellij.psi.PsiReference?,
    ): Boolean =
        elements.isNotEmpty() &&
            elements.all { it is RsScript } &&
            (targetContainer == null || targetContainer is RsFile) &&
            (targetContainer == null || isValidTarget(targetContainer, elements))

    override fun isValidTarget(
        psiElement: PsiElement?,
        sources: Array<out PsiElement>,
    ): Boolean {
        val target = psiElement as? RsFile ?: return false
        val sourceScripts = sources.filterIsInstance<RsScript>()
        if (sourceScripts.size != sources.size || sourceScripts.any { it.containingFile == target }) return false
        val targetModule = ModuleUtil.findModuleForPsiElement(target) ?: return false
        if (sourceScripts.any { ModuleUtil.findModuleForPsiElement(it) != targetModule }) return false
        val targetNames = PsiTreeUtil.getChildrenOfTypeAsList(target, RsScript::class.java).mapTo(hashSetOf(), RsScript::qualifiedName)
        return sourceScripts.none { it.qualifiedName in targetNames }
    }

    override fun doMove(
        project: Project,
        elements: Array<out PsiElement>,
        targetContainer: PsiElement?,
        callback: MoveCallback?,
    ) {
        val scripts = elements.filterIsInstance<RsScript>()
        if (scripts.size != elements.size) return
        val target = (targetContainer as? RsFile) ?: chooseTarget(project, scripts) ?: return
        if (!isValidTarget(target, elements)) return

        WriteCommandAction.runWriteCommandAction(project, "Move RuneScript scripts", null, {
            val whitespace = PsiParserFacade.getInstance(project).createWhiteSpaceFromText("\n\n")
            for (script in scripts) {
                if (target.lastChild != null && target.text.isNotBlank()) target.add(whitespace.copy())
                target.add(script.copy())
                script.delete()
            }
            callback?.refactoringCompleted()
        }, target, *scripts.map(RsScript::getContainingFile).distinct().toTypedArray())
    }

    override fun getActionName(elements: Array<out PsiElement>): String = "Move RuneScript Script"

    private fun chooseTarget(
        project: Project,
        scripts: List<RsScript>,
    ): RsFile? {
        val module = ModuleUtil.findModuleForPsiElement(scripts.first()) ?: return null
        val sourceFiles = scripts.mapTo(hashSetOf(), RsScript::getContainingFile)
        val targets =
            FileTypeIndex
                .getFiles(RsFileType, GlobalSearchScope.moduleScope(module))
                .mapNotNull { PsiManager.getInstance(project).findFile(it) as? RsFile }
                .filter { it !in sourceFiles && isValidTarget(it, scripts.toTypedArray()) }
                .sortedBy(PsiFile::getName)
        if (targets.isEmpty()) return null
        val labels: Array<String?> = targets.map { it.virtualFile?.presentableUrl ?: it.name }.toTypedArray()
        val choice =
            MessagesService.getInstance().showChooseDialog(
                project,
                null,
                "Move scripts to:",
                "Move RuneScript Script",
                labels,
                labels.first(),
                null,
            )
        return targets.getOrNull(choice)
    }
}

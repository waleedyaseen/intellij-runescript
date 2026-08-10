package io.runescript.plugin.ide.codeInsight.intention.script

import com.intellij.codeInsight.intention.BaseElementAtCaretIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.ide.codeInsight.intention.findScriptAtSignature
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.scriptNameExpression
import io.runescript.plugin.lang.psi.triggerName
import io.runescript.plugin.lang.stubs.index.RsClientScriptIndex
import io.runescript.plugin.lang.stubs.index.RsProcScriptIndex

class RsCreateScriptWrapperIntention : BaseElementAtCaretIntentionAction() {
    private var actionText: String = RsBundle.message("intention.create.script.wrapper.family.name")

    override fun getFamilyName(): String = RsBundle.message("intention.create.script.wrapper.family.name")

    override fun getText(): String = actionText

    override fun isAvailable(
        project: Project,
        editor: Editor,
        element: PsiElement,
    ): Boolean {
        val script = applicableScript(project, element) ?: return false
        actionText =
            when (script.triggerName) {
                PROC_TRIGGER -> RsBundle.message("intention.create.clientscript.wrapper.name")
                CLIENTSCRIPT_TRIGGER -> RsBundle.message("intention.extract.proc.wrapper.name")
                else -> return false
            }
        return true
    }

    override fun invoke(
        project: Project,
        editor: Editor,
        element: PsiElement,
    ) {
        val script = applicableScript(project, element) ?: return
        val document = editor.document
        val call = forwardingCall(script)
        when (script.triggerName) {
            PROC_TRIGGER -> {
                val wrapper = buildSignature(script, CLIENTSCRIPT_TRIGGER) + "\n" + call + "\n\n"
                document.insertString(script.textRange.startOffset, wrapper)
            }

            CLIENTSCRIPT_TRIGGER -> {
                val body = script.statementList.text
                val procedure = "\n\n" + buildSignature(script, PROC_TRIGGER) + body
                document.insertString(script.textRange.endOffset, procedure)
                document.replaceString(script.statementList.textRange.startOffset, script.statementList.textRange.endOffset, "\n$call")
            }
        }
        PsiDocumentManager.getInstance(project).commitDocument(document)
    }

    private fun applicableScript(
        project: Project,
        element: PsiElement,
    ): RsScript? {
        if (DumbService.isDumb(project)) return null
        val script = element.findScriptAtSignature() ?: return null
        if (script.star != null || !hasCompleteParameters(script)) return null
        val counterpartTrigger =
            when (script.triggerName) {
                PROC_TRIGGER -> {
                    if (script.returnList?.typeNameList?.isNotEmpty() == true) return null
                    CLIENTSCRIPT_TRIGGER
                }

                CLIENTSCRIPT_TRIGGER -> {
                    PROC_TRIGGER
                }

                else -> {
                    return null
                }
            }
        return script.takeUnless { counterpartExists(it, counterpartTrigger) }
    }

    private fun hasCompleteParameters(script: RsScript): Boolean =
        script.parameterList?.parameterList?.all { it.localVariableExpression != null } != false

    private fun counterpartExists(
        script: RsScript,
        trigger: String,
    ): Boolean {
        val name = script.scriptNameExpression.text
        if (
            PsiTreeUtil
                .findChildrenOfType(script.containingFile, RsScript::class.java)
                .any { it.triggerName == trigger && it.scriptNameExpression.text == name }
        ) {
            return true
        }
        val module = ModuleUtil.findModuleForPsiElement(script) ?: return false
        val index = if (trigger == PROC_TRIGGER) RsProcScriptIndex.KEY else RsClientScriptIndex.KEY
        return StubIndex.getElements(index, name, script.project, GlobalSearchScope.moduleScope(module), RsScript::class.java).isNotEmpty()
    }

    private fun buildSignature(
        script: RsScript,
        trigger: String,
    ): String = "[$trigger,${script.scriptNameExpression.text}]${script.parameterList?.text.orEmpty()}"

    private fun forwardingCall(script: RsScript): String {
        val arguments =
            script.parameterList
                ?.parameterList
                ?.map { requireNotNull(it.localVariableExpression).text }
                .orEmpty()
                .joinToString(", ")
        return "~${script.scriptNameExpression.text}($arguments);"
    }

    companion object {
        private const val PROC_TRIGGER = "proc"
        private const val CLIENTSCRIPT_TRIGGER = "clientscript"
    }
}

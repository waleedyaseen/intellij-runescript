package io.runescript.plugin.ide.codeInsight.intention.controlFlow

import com.intellij.codeInsight.intention.BaseElementAtCaretIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleManager
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.lang.psi.RsElementGenerator
import io.runescript.plugin.lang.psi.RsSwitchCase
import io.runescript.plugin.lang.psi.RsSwitchStatement

class RsSplitSwitchCaseIntention : BaseElementAtCaretIntentionAction() {
    override fun getFamilyName(): String = RsBundle.message("intention.split.switch.case.family.name")

    override fun getText(): String = RsBundle.message("intention.split.switch.case.name")

    override fun invoke(
        project: Project,
        editor: Editor,
        element: PsiElement,
    ) {
        val case = findCase(element) ?: return
        val switch = case.parent as? RsSwitchStatement ?: return
        val caseOffset = case.textOffset - switch.textOffset
        val caseEndOffset = case.textRange.endOffset - switch.textOffset
        val body = case.statementList.text.trim()
        val splitCases =
            case.expressionList.joinToString("\n") { expression ->
                "case ${expression.text} :\n$body"
            }
        val replacementText = switch.text.substring(0, caseOffset) + splitCases + switch.text.substring(caseEndOffset)
        val replacement = RsElementGenerator.createStatement(project, replacementText)
        val switchOffset = switch.textOffset
        CodeStyleManager.getInstance(project).reformat(switch.replace(replacement))
        PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.document)
        val caseKeywordOffset = editor.document.text.indexOf("case ", switchOffset + caseOffset)
        if (caseKeywordOffset >= 0) editor.caretModel.moveToOffset(caseKeywordOffset)
    }

    override fun isAvailable(
        project: Project,
        editor: Editor,
        element: PsiElement,
    ): Boolean = findCase(element)?.expressionList?.size?.let { it > 1 } == true

    private fun findCase(element: PsiElement): RsSwitchCase? {
        val case = element.parent as? RsSwitchCase ?: return null
        return case.takeIf { element == it.`case` }
    }
}

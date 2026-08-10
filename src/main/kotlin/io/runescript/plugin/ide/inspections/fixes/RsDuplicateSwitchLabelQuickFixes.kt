package io.runescript.plugin.ide.inspections.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.ide.inspections.switchLabelKey
import io.runescript.plugin.lang.psi.RsElementGenerator
import io.runescript.plugin.lang.psi.RsExpression
import io.runescript.plugin.lang.psi.RsSwitchCase
import io.runescript.plugin.lang.psi.RsSwitchStatement

class RsRemoveDuplicateSwitchLabelQuickFix(
    private val label: String,
) : LocalQuickFix {
    override fun getFamilyName(): String = "Remove duplicate switch label"

    override fun getName(): String = "Remove duplicate label '$label'"

    override fun applyFix(
        project: Project,
        descriptor: ProblemDescriptor,
    ) {
        val duplicateCase = descriptor.psiElement.parent as? RsSwitchCase ?: return
        val switch = duplicateCase.parent as? RsSwitchStatement ?: return
        val values = duplicateCase.expressionList.filterNot { it == descriptor.psiElement }
        val replacement =
            values.takeIf { it.isNotEmpty() }?.let {
                "case ${it.joinToString(", ") { value -> value.text }} :\n${duplicateCase.statementList.text.trim()}"
            }
        replaceCaseRanges(project, switch, mapOf(duplicateCase to replacement))
    }
}

class RsMergeDuplicateSwitchCasesQuickFix(
    private val label: String,
) : LocalQuickFix {
    override fun getFamilyName(): String = "Merge duplicate switch cases"

    override fun getName(): String = "Merge cases containing '$label'"

    override fun applyFix(
        project: Project,
        descriptor: ProblemDescriptor,
    ) {
        val duplicateCase = descriptor.psiElement.parent as? RsSwitchCase ?: return
        val duplicateExpression = descriptor.psiElement as? RsExpression ?: return
        val switch = duplicateCase.parent as? RsSwitchStatement ?: return
        val duplicateIndex = switch.switchCaseList.indexOf(duplicateCase)
        val originalCase =
            switch.switchCaseList
                .take(duplicateIndex)
                .firstOrNull { case -> case.expressionList.any { it.switchLabelKey() == duplicateExpression.switchLabelKey() } }
                ?: return
        if (originalCase.statementList.text.trim() != duplicateCase.statementList.text.trim()) return
        val mergedValues =
            (originalCase.expressionList + duplicateCase.expressionList)
                .distinctBy { it.switchLabelKey() }
                .joinToString(", ") { it.text }
        replaceCaseRanges(
            project,
            switch,
            mapOf(
                originalCase to "case $mergedValues :\n${originalCase.statementList.text.trim()}",
                duplicateCase to null,
            ),
        )
    }
}

private fun replaceCaseRanges(
    project: Project,
    switch: RsSwitchStatement,
    replacements: Map<RsSwitchCase, String?>,
) {
    val switchOffset = switch.textOffset
    val replacementText =
        replacements.entries
            .sortedByDescending { it.key.textOffset }
            .fold(switch.text) { text, (case, replacement) ->
                text.replaceRange(
                    case.textOffset - switchOffset,
                    case.textRange.endOffset - switchOffset,
                    replacement.orEmpty(),
                )
            }
    val replacement = RsElementGenerator.createStatement(project, replacementText)
    CodeStyleManager.getInstance(project).reformat(switch.replace(replacement))
}

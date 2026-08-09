package io.runescript.plugin.ide.inspections.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import io.runescript.plugin.lang.psi.RsCallExpression
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.arguments
import io.runescript.plugin.lang.psi.typechecker.diagnostics.Diagnostic
import io.runescript.plugin.lang.psi.typechecker.diagnostics.DiagnosticMessage
import io.runescript.plugin.lang.psi.typechecker.type.TupleType
import io.runescript.plugin.lang.psi.typechecker.typeCheckedType

class RsRemoveExtraArgumentsQuickFix private constructor(
    private val firstExtraArgument: Int,
    private val extraArgumentCount: Int,
) : LocalQuickFix {
    override fun getName(): String =
        if (extraArgumentCount == 1) {
            "Remove extra argument"
        } else {
            "Remove $extraArgumentCount extra arguments"
        }

    override fun getFamilyName(): String = "Remove extra arguments"

    override fun applyFix(
        project: Project,
        descriptor: ProblemDescriptor,
    ) {
        val call = descriptor.psiElement as? RsCallExpression ?: return
        val argumentList = call.argumentList ?: return
        val arguments = argumentList.expressionList
        val closingParenthesis = argumentList.rparen ?: return
        if (firstExtraArgument !in arguments.indices) return
        val startOffset =
            if (firstExtraArgument == 0) {
                argumentList.lparen.textRange.endOffset
            } else {
                arguments[firstExtraArgument - 1].textRange.endOffset
            }
        val document = PsiDocumentManager.getInstance(project).getDocument(call.containingFile) ?: return
        document.deleteString(startOffset, closingParenthesis.textOffset)
        PsiDocumentManager.getInstance(project).commitDocument(document)
    }

    companion object {
        fun from(diagnostic: Diagnostic): RsRemoveExtraArgumentsQuickFix? {
            val call = diagnostic.element as? RsCallExpression ?: return null
            val expectedCount =
                when (diagnostic.message) {
                    DiagnosticMessage.COMMAND_NOARGS_EXPECTED,
                    DiagnosticMessage.PROC_NOARGS_EXPECTED,
                    DiagnosticMessage.CLIENTSCRIPT_NOARGS_EXPECTED,
                    -> {
                        0
                    }

                    DiagnosticMessage.GENERIC_TYPE_MISMATCH -> {
                        (call.reference?.resolve() as? RsScript)
                            ?.parameterList
                            ?.parameterList
                            .orEmpty()
                            .size
                    }

                    else -> {
                        return null
                    }
                }
            var consumed = 0
            for ((index, argument) in call.arguments.withIndex()) {
                val argumentCount = TupleType.toList(argument.typeCheckedType).size
                if (consumed >= expectedCount) {
                    return RsRemoveExtraArgumentsQuickFix(index, call.arguments.size - index)
                }
                if (consumed + argumentCount > expectedCount) return null
                consumed += argumentCount
            }
            return null
        }
    }
}

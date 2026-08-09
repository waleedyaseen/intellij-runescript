package io.runescript.plugin.ide.inspections.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import io.runescript.plugin.ide.neptune.typeManager
import io.runescript.plugin.lang.psi.RsCallExpression
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.arguments
import io.runescript.plugin.lang.psi.typechecker.diagnostics.Diagnostic
import io.runescript.plugin.lang.psi.typechecker.diagnostics.DiagnosticMessage
import io.runescript.plugin.lang.psi.typechecker.type.PrimitiveType
import io.runescript.plugin.lang.psi.typechecker.type.TupleType
import io.runescript.plugin.lang.psi.typechecker.type.Type
import io.runescript.plugin.lang.psi.typechecker.typeCheckedType

class RsInsertMissingArgumentsQuickFix private constructor(
    private val defaults: List<String>,
) : LocalQuickFix {
    override fun getName(): String =
        if (defaults.size == 1) {
            "Insert missing argument"
        } else {
            "Insert ${defaults.size} missing arguments"
        }

    override fun getFamilyName(): String = "Insert missing arguments"

    override fun applyFix(
        project: Project,
        descriptor: ProblemDescriptor,
    ) {
        val call = descriptor.psiElement as? RsCallExpression ?: return
        val arguments = call.argumentList ?: return
        val closingParenthesis = arguments.rparen ?: return
        val document = PsiDocumentManager.getInstance(project).getDocument(call.containingFile) ?: return
        val prefix = if (arguments.expressionList.isEmpty()) "" else ", "
        document.insertString(closingParenthesis.textOffset, prefix + defaults.joinToString(", "))
        PsiDocumentManager.getInstance(project).commitDocument(document)
    }

    companion object {
        fun from(diagnostic: Diagnostic): RsInsertMissingArgumentsQuickFix? {
            if (diagnostic.message != DiagnosticMessage.GENERIC_TYPE_MISMATCH) return null
            val call = diagnostic.element as? RsCallExpression ?: return null
            val target = call.reference?.resolve() as? RsScript ?: return null
            val parameterTypes =
                target.parameterList
                    ?.parameterList
                    .orEmpty()
                    .map { parameter ->
                        call.typeManager.findOrNull(parameter.typeName.text, allowArray = true) ?: return null
                    }
            val actualCount =
                call.arguments.sumOf { argument ->
                    TupleType.toList(argument.typeCheckedType).size
                }
            if (actualCount >= parameterTypes.size) return null
            return RsInsertMissingArgumentsQuickFix(parameterTypes.drop(actualCount).map(::defaultExpression))
        }

        private fun defaultExpression(type: Type): String =
            when (type) {
                PrimitiveType.BOOLEAN -> "false"
                PrimitiveType.STRING -> "\"\""
                else -> type.defaultValue?.toString() ?: "null"
            }
    }
}

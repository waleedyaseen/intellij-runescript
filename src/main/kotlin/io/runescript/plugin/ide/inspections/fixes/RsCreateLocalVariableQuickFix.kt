package io.runescript.plugin.ide.inspections.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.util.parentOfType
import io.runescript.plugin.ide.neptune.typeManager
import io.runescript.plugin.lang.psi.RsLocalVariableExpression
import io.runescript.plugin.lang.psi.RsStatement
import io.runescript.plugin.lang.psi.typechecker.diagnostics.Diagnostic
import io.runescript.plugin.lang.psi.typechecker.diagnostics.DiagnosticMessage
import io.runescript.plugin.lang.psi.typechecker.type.PrimitiveType
import io.runescript.plugin.lang.psi.typechecker.type.TupleType
import io.runescript.plugin.lang.psi.typechecker.type.Type
import io.runescript.plugin.lang.psi.typechecker.type.wrapped.ArrayType
import io.runescript.plugin.lang.psi.typechecker.typeHint

class RsCreateLocalVariableQuickFix private constructor(
    private val variableName: String,
    private val typeName: String,
    private val initializer: String,
) : LocalQuickFix {
    override fun getName(): String = "Create local variable '$$variableName'"

    override fun getFamilyName(): String = "Create local variable"

    override fun applyFix(
        project: Project,
        descriptor: ProblemDescriptor,
    ) {
        val reference = descriptor.psiElement as? RsLocalVariableExpression ?: return
        val statement = reference.parentOfType<RsStatement>() ?: return
        val document = PsiDocumentManager.getInstance(project).getDocument(reference.containingFile) ?: return
        val lineNumber = document.getLineNumber(statement.textOffset)
        val lineStart = document.getLineStartOffset(lineNumber)
        val indentation = document.getText(TextRange(lineStart, statement.textOffset))
        if (indentation.any { character -> !character.isWhitespace() }) return
        val declaration = "def_$typeName $$variableName = $initializer;\n$indentation"
        document.insertString(statement.textOffset, declaration)
        PsiDocumentManager.getInstance(project).commitDocument(document)
    }

    companion object {
        fun from(diagnostic: Diagnostic): RsCreateLocalVariableQuickFix? {
            if (diagnostic.message != DiagnosticMessage.LOCAL_REFERENCE_UNRESOLVED) return null
            val reference = diagnostic.element as? RsLocalVariableExpression ?: return null
            val expectedType = reference.typeHint ?: return null
            if (expectedType is TupleType || expectedType is ArrayType) return null
            if (!reference.typeManager.getOptions(expectedType).allowDeclaration) return null
            return RsCreateLocalVariableQuickFix(
                reference.nameLiteral.text,
                expectedType.representation,
                defaultExpression(expectedType),
            )
        }

        private fun defaultExpression(type: Type): String =
            when (type) {
                PrimitiveType.BOOLEAN -> "false"
                PrimitiveType.STRING -> "\"\""
                else -> type.defaultValue?.toString() ?: "null"
            }
    }
}

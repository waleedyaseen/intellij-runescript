package io.runescript.plugin.ide.inspections.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.util.parentOfType
import io.runescript.plugin.ide.neptune.typeManager
import io.runescript.plugin.lang.psi.RsLocalVariableDeclarationStatement
import io.runescript.plugin.lang.psi.typechecker.diagnostics.Diagnostic
import io.runescript.plugin.lang.psi.typechecker.diagnostics.DiagnosticMessage
import io.runescript.plugin.lang.psi.typechecker.type.MetaType
import io.runescript.plugin.lang.psi.typechecker.type.TupleType
import io.runescript.plugin.lang.psi.typechecker.type.wrapped.ArrayType
import io.runescript.plugin.lang.psi.typechecker.typeCheckedType

class RsChangeLocalVariableTypeQuickFix private constructor(
    private val typeName: String,
) : LocalQuickFix {
    override fun getName(): String = "Change local type to '$typeName'"

    override fun getFamilyName(): String = "Change local variable type"

    override fun applyFix(
        project: Project,
        descriptor: ProblemDescriptor,
    ) {
        val declaration = descriptor.psiElement.parentOfType<RsLocalVariableDeclarationStatement>() ?: return
        val document = PsiDocumentManager.getInstance(project).getDocument(declaration.containingFile) ?: return
        document.replaceString(
            declaration.defineType.textRange.startOffset,
            declaration.defineType.textRange.endOffset,
            "def_$typeName",
        )
        PsiDocumentManager.getInstance(project).commitDocument(document)
    }

    companion object {
        fun from(diagnostic: Diagnostic): RsChangeLocalVariableTypeQuickFix? {
            if (diagnostic.message != DiagnosticMessage.GENERIC_TYPE_MISMATCH) return null
            val declaration =
                diagnostic.element.parentOfType<RsLocalVariableDeclarationStatement>() ?: return null
            val initializer = declaration.initializer ?: return null
            if (diagnostic.element !== initializer) return null
            val actualType = initializer.typeCheckedType
            if (actualType is MetaType || actualType is TupleType || actualType is ArrayType) return null
            if (!declaration.typeManager.getOptions(actualType).allowDeclaration) return null
            val currentType = declaration.defineType.text.removePrefix("def_")
            if (currentType == actualType.representation) return null
            return RsChangeLocalVariableTypeQuickFix(actualType.representation)
        }
    }
}

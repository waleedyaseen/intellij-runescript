package io.runescript.plugin.ide.inspections.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessModuleDir
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.parentOfType
import io.runescript.plugin.ide.neptune.neptuneModuleData
import io.runescript.plugin.ide.neptune.typeManager
import io.runescript.plugin.lang.psi.RsConstantExpression
import io.runescript.plugin.lang.psi.typechecker.diagnostics.Diagnostic
import io.runescript.plugin.lang.psi.typechecker.diagnostics.DiagnosticMessage
import io.runescript.plugin.lang.psi.typechecker.type.PrimitiveType
import io.runescript.plugin.lang.psi.typechecker.type.TupleType
import io.runescript.plugin.lang.psi.typechecker.type.Type
import io.runescript.plugin.lang.psi.typechecker.type.wrapped.ArrayType
import io.runescript.plugin.lang.psi.typechecker.typeCheckedType
import io.runescript.plugin.lang.psi.typechecker.typeHint
import io.runescript.plugin.symbollang.psi.resolveToSymTypeName

class RsCreateConstantQuickFix private constructor(
    private val constantName: String,
    private val typeName: String,
    private val value: String,
) : LocalQuickFix {
    override fun getName(): String = "Create constant '^$constantName'"

    override fun getFamilyName(): String = "Create constant"

    override fun applyFix(
        project: Project,
        descriptor: ProblemDescriptor,
    ) {
        val reference = descriptor.psiElement.parentOfType<RsConstantExpression>(withSelf = true) ?: return
        val module = ModuleUtil.findModuleForPsiElement(reference) ?: return
        val virtualFile =
            FilenameIndex
                .getVirtualFilesByName(CONSTANT_FILE_NAME, GlobalSearchScope.moduleScope(module))
                .firstOrNull { file ->
                    resolveToSymTypeName(PsiManager.getInstance(project).findFile(file)) == "constant"
                }
                ?: createConstantFile(module) ?: return
        val file = PsiManager.getInstance(project).findFile(virtualFile) ?: return
        val document = PsiDocumentManager.getInstance(project).getDocument(file) ?: return
        val prefix = if (document.textLength == 0 || document.text.endsWith("\n")) "" else "\n"
        document.insertString(document.textLength, "$prefix$constantName\t$typeName\t$value\n")
        PsiDocumentManager.getInstance(project).commitDocument(document)
    }

    companion object {
        private const val CONSTANT_FILE_NAME = "constant.sym"

        fun from(diagnostic: Diagnostic): RsCreateConstantQuickFix? {
            if (diagnostic.message != DiagnosticMessage.CONSTANT_REFERENCE_UNRESOLVED) return null
            val reference = diagnostic.element.parentOfType<RsConstantExpression>(withSelf = true) ?: return null
            val expectedType = reference.typeHint ?: reference.typeCheckedType
            if (expectedType is TupleType || expectedType is ArrayType) return null
            if (!reference.typeManager.getOptions(expectedType).allowDeclaration) return null
            val module = ModuleUtil.findModuleForPsiElement(reference) ?: return null
            if (module.neptuneModuleData.symbolPaths.isEmpty()) return null
            return RsCreateConstantQuickFix(
                reference.name ?: return null,
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

        private fun createConstantFile(module: com.intellij.openapi.module.Module): VirtualFile? {
            val moduleDirectory = module.guessModuleDir() ?: return null
            val symbolsDirectory =
                module.neptuneModuleData.symbolPaths
                    .asSequence()
                    .mapNotNull(moduleDirectory::findFileByRelativePath)
                    .firstOrNull() ?: return null
            return symbolsDirectory.findChild(CONSTANT_FILE_NAME)
                ?: symbolsDirectory.createChildData(RsCreateConstantQuickFix::class.java, CONSTANT_FILE_NAME)
        }
    }
}

package io.runescript.plugin.ide.refactoring

import com.intellij.codeInsight.highlighting.ReadWriteAccessDetector.Access
import com.intellij.codeInsight.template.Expression
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.refactoring.RefactoringActionHandler
import com.intellij.refactoring.rename.inplace.MemberInplaceRenamer
import com.intellij.refactoring.util.CommonRefactoringUtil
import io.runescript.plugin.ide.usages.RsReadWriteAccessDetector
import io.runescript.plugin.lang.psi.RsLocalVariableExpression
import io.runescript.plugin.lang.psi.RsParameter
import io.runescript.plugin.lang.psi.RsReturnStatement
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.RsStatement
import io.runescript.plugin.lang.psi.RsStatementList
import io.runescript.plugin.lang.psi.isForArrayDeclaration
import io.runescript.plugin.lang.psi.isForVariableDeclaration
import io.runescript.plugin.lang.psi.typechecker.type.MetaType
import io.runescript.plugin.lang.psi.typechecker.type.TupleType
import io.runescript.plugin.lang.psi.typechecker.typeCheckedType

class RsExtractProcedureHandler : RefactoringActionHandler {
    override fun invoke(
        project: Project,
        editor: Editor,
        file: PsiFile,
        dataContext: DataContext,
    ) {
        if (!CommonRefactoringUtil.checkReadOnlyStatus(file)) return
        PsiDocumentManager.getInstance(project).commitDocument(editor.document)
        val extraction = analyze(editor, file) ?: return

        val extractedName = extraction.procedureName
        WriteCommandAction.runWriteCommandAction(project, "Extract Script", null, {
            val document = editor.document
            document.insertString(extraction.script.textRange.endOffset, "\n\n${extraction.procedureText}")
            document.replaceString(extraction.range.startOffset, extraction.range.endOffset, extraction.callText)
            PsiDocumentManager.getInstance(project).commitDocument(document)
            CodeStyleManager.getInstance(project).reformat(file)
            PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(document)
            editor.selectionModel.removeSelection()
        }, file)
        val extractedScript =
            PsiTreeUtil
                .getChildrenOfTypeAsList(file, RsScript::class.java)
                .firstOrNull { it.name == extractedName } ?: return
        editor.caretModel.moveToOffset(extractedScript.textOffset)
        object : MemberInplaceRenamer(
            extractedScript,
            extractedScript,
            editor,
        ) {
            override fun shouldStopAtLookupExpression(expression: Expression): Boolean = true
        }.performInplaceRefactoring(linkedSetOf())
    }

    override fun invoke(
        project: Project,
        elements: Array<out PsiElement?>,
        dataContext: DataContext?,
    ) = Unit

    internal fun analyze(
        editor: Editor,
        file: PsiFile,
    ): RsProcedureExtraction? {
        val selection = editor.selectionModel
        if (!selection.hasSelection()) return null
        val selectedRange = TextRange(selection.selectionStart, selection.selectionEnd)
        val start = file.findElementAt(selectedRange.startOffset) ?: return null
        val end = file.findElementAt(selectedRange.endOffset - 1) ?: return null
        val common = PsiTreeUtil.findCommonParent(start, end) ?: return null
        val statementList = PsiTreeUtil.getParentOfType(common, RsStatementList::class.java, false) ?: return null
        val statements =
            statementList.statementList.filter { statement ->
                selectedRange.contains(statement.textRange)
            }
        if (statements.isEmpty()) return null
        val range = TextRange(statements.first().textRange.startOffset, statements.last().textRange.endOffset)
        if (file.text.substring(selectedRange.startOffset, range.startOffset).isNotBlank() ||
            file.text.substring(range.endOffset, selectedRange.endOffset).isNotBlank()
        ) {
            return null
        }
        if (statements.any {
                it is RsReturnStatement || PsiTreeUtil.findChildOfType(
                    it,
                    RsReturnStatement::class.java,
                ) != null
            }
        ) {
            return null
        }
        val script = PsiTreeUtil.getParentOfType(statementList, RsScript::class.java) ?: return null
        val accessDetector = RsReadWriteAccessDetector()
        val locals = statements.flatMap { PsiTreeUtil.findChildrenOfType(it, RsLocalVariableExpression::class.java) }

        val inputs = linkedMapOf<RsLocalVariableExpression, RsExtractedVariable>()
        val written = linkedMapOf<RsLocalVariableExpression, RsExtractedVariable>()
        for (local in locals.sortedBy(PsiElement::getTextOffset)) {
            if (local.isDeclaration()) continue
            val declaration = local.reference?.resolve() as? RsLocalVariableExpression ?: continue
            val variable = declaration.extractedVariable() ?: return null
            when (accessDetector.getExpressionAccess(local)) {
                Access.Read -> {
                    if (!range.contains(declaration.textOffset) && declaration !in written) {
                        inputs.putIfAbsent(declaration, variable)
                    }
                }

                Access.Write -> {
                    written.putIfAbsent(declaration, variable)
                }

                Access.ReadWrite -> {
                    if (!range.contains(declaration.textOffset) && declaration !in written) {
                        inputs.putIfAbsent(declaration, variable)
                    }
                    written.putIfAbsent(declaration, variable)
                }
            }
        }

        val referencesAfter =
            PsiTreeUtil
                .findChildrenOfType(script, RsLocalVariableExpression::class.java)
                .asSequence()
                .filter { it.textOffset > range.endOffset && !it.isDeclaration() }
                .mapNotNull { it.reference?.resolve() as? RsLocalVariableExpression }
                .toSet()
        val declaredInside =
            locals
                .filter { it.isDeclaration() }
                .associateWith { it.extractedVariable() ?: return null }
        val outputs =
            (written + declaredInside)
                .filterKeys { it in referencesAfter }
                .values
                .distinctBy(RsExtractedVariable::name)

        val procedureName = uniqueProcedureName(file, "extracted")
        val arguments = inputs.values.joinToString(", ") { "$${it.name}" }
        val call = "~$procedureName($arguments)"
        val declarations =
            outputs
                .filter { output -> declaredInside.values.any { it.name == output.name } }
                .joinToString("\n") { "def_${it.type} $${it.name};" }
        val invocation =
            if (outputs.isEmpty()) {
                "$call;"
            } else {
                outputs.joinToString(", ") { "$${it.name}" } + " = $call;"
            }
        val callText = listOf(declarations, invocation).filter(String::isNotEmpty).joinToString("\n")

        val parameters = inputs.values.joinToString(", ") { "${it.type} $${it.name}" }
        val returns = outputs.joinToString(", ") { it.type }
        val selectedText = file.text.substring(range.startOffset, range.endOffset).trimIndent()
        val body =
            buildString {
                appendLine("{")
                appendLine(selectedText.prependIndent("    "))
                if (outputs.isNotEmpty()) {
                    appendLine("    return(${outputs.joinToString(", ") { "$${it.name}" }});")
                }
                append("}")
            }
        val procedureText =
            buildString {
                append("[proc,$procedureName]")
                if (parameters.isNotEmpty()) append("($parameters)")
                if (returns.isNotEmpty()) append("($returns)")
                appendLine()
                append(body)
            }
        return RsProcedureExtraction(script, range, procedureName, callText, procedureText)
    }

    private fun RsLocalVariableExpression.extractedVariable(): RsExtractedVariable? {
        val type = typeCheckedType
        if (type is MetaType.Error || type is MetaType.Unit || type is TupleType) return null
        return RsExtractedVariable(name ?: return null, type.representation)
    }

    private fun RsLocalVariableExpression.isDeclaration(): Boolean =
        isForVariableDeclaration() || isForArrayDeclaration() || parent is RsParameter

    private fun uniqueProcedureName(
        file: PsiFile,
        base: String,
    ): String {
        val names = PsiTreeUtil.findChildrenOfType(file, RsScript::class.java).mapNotNullTo(hashSetOf(), RsScript::getName)
        if (base !in names) return base
        var suffix = 2
        while ("${base}_$suffix" in names) suffix++
        return "${base}_$suffix"
    }
}

internal data class RsExtractedVariable(
    val name: String,
    val type: String,
)

internal data class RsProcedureExtraction(
    val script: RsScript,
    val range: TextRange,
    val procedureName: String,
    val callText: String,
    val procedureText: String,
)

package io.runescript.plugin.ide.refactoring

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.ide.codeInsight.intention.documentation.RsAddMissingDocTagsIntention
import io.runescript.plugin.ide.codeInsight.intention.documentation.RsRemoveObsoleteDocTagsIntention
import io.runescript.plugin.ide.codeInsight.intention.documentation.RsReorderDocTagsIntention
import io.runescript.plugin.ide.doc.findDoc
import io.runescript.plugin.ide.neptune.typeManager
import io.runescript.plugin.lang.psi.RsCallExpression
import io.runescript.plugin.lang.psi.RsElementGenerator
import io.runescript.plugin.lang.psi.RsLocalVariableExpression
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.arguments

internal data class RsSignatureParameter(
    val typeName: String,
    val name: String,
    val newArgumentValue: String?,
    val originalIndex: Int?,
)

internal data class RsSignatureChange(
    val parameters: List<RsSignatureParameter>,
    val returnTypes: List<String>,
)

internal object RsChangeSignatureProcessor {
    fun returnEditorText(script: RsScript): String =
        script.returnList
            ?.typeNameList
            .orEmpty()
            .joinToString(", ") { it.text }

    fun parseChange(
        project: Project,
        script: RsScript,
        parameterText: String,
        returnText: String,
    ): ParseResult {
        val oldParameters = script.parameterList?.parameterList.orEmpty()
        val rows =
            parameterText
                .lines()
                .map(String::trim)
                .filter(String::isNotEmpty)
        val parsedRows = mutableListOf<ParsedParameter>()
        for ((index, row) in rows.withIndex()) {
            val match =
                PARAMETER_ROW.matchEntire(row)
                    ?: return ParseResult.Error(
                        "Invalid parameter on line ${index + 1}: '$row'. Use 'type ${'$'}name' or 'type ${'$'}name = default'.",
                    )
            parsedRows += ParsedParameter(match.groupValues[1], match.groupValues[2], match.groupValues[3].takeIf(String::isNotBlank))
        }
        val oldNames = oldParameters.map { parameter -> parameter.localVariableExpression?.name.orEmpty() }
        val unusedOldIndices = oldParameters.indices.toMutableSet()
        val parameters =
            parsedRows.mapIndexed { newIndex, row ->
                val matchingIndex = oldNames.indices.firstOrNull { index -> index in unusedOldIndices && oldNames[index] == row.name }
                val originalIndex =
                    matchingIndex
                        ?: newIndex.takeIf { index -> row.newArgumentValue == null && index in unusedOldIndices }
                if (originalIndex != null) unusedOldIndices.remove(originalIndex)
                RsSignatureParameter(row.typeName, row.name, row.newArgumentValue, originalIndex)
            }
        return createChange(project, script, parameters, returnText)
    }

    fun createChange(
        project: Project,
        script: RsScript,
        parameters: List<RsSignatureParameter>,
        returnText: String,
    ): ParseResult {
        val duplicate =
            parameters
                .groupingBy(RsSignatureParameter::name)
                .eachCount()
                .entries
                .firstOrNull { it.value > 1 }
                ?.key
        if (duplicate != null) return ParseResult.Error("Duplicate parameter '$duplicate'.")
        val validator = RsNamesValidator()
        parameters.firstOrNull { row -> !validator.isIdentifier(row.name, project) || validator.isKeyword(row.name, project) }?.let {
            return ParseResult.Error("'${it.name}' is not a valid RuneScript parameter name.")
        }
        parameters.firstOrNull { row -> row.typeName !in script.typeManager.typeKeywords }?.let {
            return ParseResult.Error("'${it.typeName}' is not a valid RuneScript type.")
        }
        parameters.firstOrNull { row -> row.newArgumentValue?.let { !isValidExpression(project, it) } == true }?.let {
            return ParseResult.Error("'${it.newArgumentValue}' is not a valid RuneScript argument value.")
        }
        val oldParameterCount = script.parameterList?.parameterList?.size ?: 0
        val originalIndices = parameters.mapNotNull(RsSignatureParameter::originalIndex)
        if (
            originalIndices.any { it !in 0 until oldParameterCount } ||
            originalIndices.size != originalIndices.distinct().size
        ) {
            return ParseResult.Error(
                "The parameter list no longer matches the current script signature. Reopen Change Signature and try again.",
            )
        }
        parameters.firstOrNull { it.originalIndex == null && it.newArgumentValue == null }?.let {
            return ParseResult.Error("New parameter '${it.name}' requires a value for existing calls.")
        }

        val returnTypes =
            returnText
                .split(',', '\n')
                .map(String::trim)
                .filter(String::isNotEmpty)
        val oldReturnCount = script.returnList?.typeNameList?.size ?: 0
        returnTypes.firstOrNull { typeName -> typeName !in script.typeManager.typeKeywords }?.let {
            return ParseResult.Error("'$it' is not a valid RuneScript return type.")
        }
        if (returnTypes.size != oldReturnCount) {
            return ParseResult.Error("Adding or removing return values is not supported; keep $oldReturnCount return value(s).")
        }
        return ParseResult.Success(RsSignatureChange(parameters, returnTypes))
    }

    fun validate(
        script: RsScript,
        change: RsSignatureChange,
    ): String? {
        val oldParameters = script.parameterList?.parameterList.orEmpty()
        val retainedIndices = change.parameters.mapNotNullTo(hashSetOf(), RsSignatureParameter::originalIndex)
        for (oldIndex in oldParameters.indices) {
            if (oldIndex in retainedIndices) continue
            val declaration = oldParameters[oldIndex].localVariableExpression ?: continue
            if (bodyReferences(script, declaration).isNotEmpty()) {
                return "Cannot remove '$${declaration.name}'; it is still used in the script body."
            }
        }
        for (call in findCalls(script)) {
            val argumentList = call.argumentList
            val argumentCount = argumentList?.expressionList?.size ?: 0
            if (argumentCount != oldParameters.size) {
                return "Cannot change signature while call '${call.text}' has $argumentCount argument(s); expected ${oldParameters.size}."
            }
            if (argumentList != null && argumentList.rparen == null) {
                return "Cannot change signature while call '${call.text}' is incomplete."
            }
        }
        return null
    }

    fun apply(
        project: Project,
        script: RsScript,
        change: RsSignatureChange,
    ): String? {
        val validationError = validate(script, change)
        if (validationError != null) return validationError
        val pointer = SmartPointerManager.createPointer(script)

        WriteCommandAction.runWriteCommandAction(project, "Change RuneScript Signature", null, {
            renameParameterReferences(script, change)
            finishPsiEdits(project, script.containingFile)

            val scriptAfterRename = pointer.element ?: return@runWriteCommandAction
            renameDocumentationSubjects(project, scriptAfterRename, change)
            PsiDocumentManager.getInstance(project).commitAllDocuments()

            val renamedScript = pointer.element ?: return@runWriteCommandAction
            updateCalls(project, renamedScript, change)
            PsiDocumentManager.getInstance(project).commitAllDocuments()

            val updatedScript = pointer.element ?: return@runWriteCommandAction
            updateDeclaration(project, updatedScript, change)
            PsiDocumentManager.getInstance(project).commitAllDocuments()

            val finalScript = pointer.element ?: return@runWriteCommandAction
            synchronizeDocumentation(project, finalScript)
        }, script.containingFile)
        return null
    }

    private fun renameParameterReferences(
        script: RsScript,
        change: RsSignatureChange,
    ) {
        val oldParameters = script.parameterList?.parameterList.orEmpty()
        val renames =
            change.parameters.mapNotNull { parameter ->
                val oldIndex = parameter.originalIndex ?: return@mapNotNull null
                val oldName = oldParameters.getOrNull(oldIndex)?.localVariableExpression?.name ?: return@mapNotNull null
                (oldIndex to oldName).takeIf { oldName != parameter.name }?.let { Triple(oldIndex, oldName, parameter.name) }
            }
        if (renames.isEmpty()) return

        for ((oldIndex, _, newName) in renames) {
            val declaration = oldParameters[oldIndex].localVariableExpression ?: continue
            for (reference in bodyReferences(script, declaration)) {
                reference.setName(newName)
            }
        }
    }

    private fun renameDocumentationSubjects(
        project: Project,
        script: RsScript,
        change: RsSignatureChange,
    ) {
        val oldParameters = script.parameterList?.parameterList.orEmpty()
        val renames =
            change.parameters.mapNotNull { parameter ->
                val oldIndex = parameter.originalIndex ?: return@mapNotNull null
                val currentName = oldParameters.getOrNull(oldIndex)?.localVariableExpression?.name ?: return@mapNotNull null
                currentName.takeIf { it != parameter.name }?.let { parameter.name to currentName }
            }
        if (renames.isEmpty()) return
        val doc = script.findDoc() ?: return
        val document = PsiDocumentManager.getInstance(project).getDocument(script.containingFile) ?: return
        var updatedDoc = doc.text
        for ((newName, oldName) in renames) {
            val subject = Regex("(?m)(@param(?:meta)?\\s+)${Regex.escape(oldName)}(?=\\s|$)")
            updatedDoc = subject.replace(updatedDoc) { match -> match.groupValues[1] + newName }
        }
        if (updatedDoc != doc.text) {
            document.replaceString(doc.textRange.startOffset, doc.textRange.endOffset, updatedDoc)
        }
    }

    private fun updateCalls(
        project: Project,
        script: RsScript,
        change: RsSignatureChange,
    ) {
        val edits = mutableListOf<TextEdit>()
        for (call in findCalls(script)) {
            val oldArguments = call.arguments.map(PsiElement::getText)
            val newArguments =
                change.parameters.map { parameter ->
                    parameter.originalIndex?.let(oldArguments::get) ?: parameter.newArgumentValue.orEmpty()
                }
            val replacement = "(${newArguments.joinToString(", ")})"
            val argumentList = call.argumentList
            if (argumentList != null) {
                edits +=
                    TextEdit(document(project, call), argumentList.textRange.startOffset, argumentList.textRange.endOffset, replacement)
            } else {
                val offset = call.nameLiteral?.textRange?.endOffset ?: continue
                edits += TextEdit(document(project, call), offset, offset, replacement)
            }
        }
        applyEdits(edits)
    }

    private fun updateDeclaration(
        project: Project,
        script: RsScript,
        change: RsSignatureChange,
    ) {
        val document = document(project, script)
        val parameterText =
            change.parameters.joinToString(", ", prefix = "(", postfix = ")") { parameter ->
                "${parameter.typeName} $${parameter.name}"
            }
        val parameterList = script.parameterList
        val returnList = script.returnList
        val rangeStart = parameterList?.textRange?.startOffset ?: script.rbracket.textRange.endOffset
        val rangeEnd = returnList?.textRange?.endOffset ?: parameterList?.textRange?.endOffset ?: rangeStart
        val includeParameterList = parameterList != null || change.parameters.isNotEmpty() || change.returnTypes.isNotEmpty()
        val replacement =
            buildString {
                if (includeParameterList) append(parameterText)
                if (returnList != null || change.returnTypes.isNotEmpty()) {
                    append(change.returnTypes.joinToString(", ", prefix = "(", postfix = ")"))
                }
            }
        document.replaceString(rangeStart, rangeEnd, replacement)
    }

    private fun synchronizeDocumentation(
        project: Project,
        script: RsScript,
    ) {
        val document = PsiDocumentManager.getInstance(project).getDocument(script.containingFile) ?: return
        RsRemoveObsoleteDocTagsIntention().applyTo(project, document, script)
        RsAddMissingDocTagsIntention().applyTo(project, document, script)
        RsReorderDocTagsIntention().applyTo(project, document, script)
    }

    private fun bodyReferences(
        script: RsScript,
        declaration: RsLocalVariableExpression,
    ): List<RsLocalVariableExpression> =
        PsiTreeUtil
            .findChildrenOfType(script.statementList, RsLocalVariableExpression::class.java)
            .filter { candidate -> candidate.reference?.resolve() === declaration }

    private fun findCalls(script: RsScript): List<RsCallExpression> =
        ReferencesSearch
            .search(script)
            .findAll()
            .mapNotNull { reference -> reference.element as? RsCallExpression }
            .distinct()

    private fun document(
        project: Project,
        element: PsiElement,
    ): Document = PsiDocumentManager.getInstance(project).getDocument(element.containingFile)!!

    private fun finishPsiEdits(
        project: Project,
        file: PsiElement,
    ) {
        val manager = PsiDocumentManager.getInstance(project)
        manager.getDocument(file.containingFile)?.let(manager::doPostponedOperationsAndUnblockDocument)
        manager.commitAllDocuments()
    }

    private fun applyEdits(edits: List<TextEdit>) {
        edits
            .groupBy(TextEdit::document)
            .forEach { (_, documentEdits) ->
                documentEdits.sortedByDescending(TextEdit::startOffset).forEach { edit ->
                    edit.document.replaceString(edit.startOffset, edit.endOffset, edit.replacement)
                }
            }
    }

    private fun isValidExpression(
        project: Project,
        text: String,
    ): Boolean =
        runCatching {
            val expression = RsElementGenerator.createExpression(project, text)
            expression.text == text && PsiTreeUtil.findChildOfType(expression, PsiErrorElement::class.java) == null
        }.getOrDefault(false)

    sealed interface ParseResult {
        data class Success(
            val change: RsSignatureChange,
        ) : ParseResult

        data class Error(
            val message: String,
        ) : ParseResult
    }

    private data class ParsedParameter(
        val typeName: String,
        val name: String,
        val newArgumentValue: String?,
    )

    private data class TextEdit(
        val document: Document,
        val startOffset: Int,
        val endOffset: Int,
        val replacement: String,
    )

    private val PARAMETER_ROW = Regex("([^\\s]+)\\s+\\$?([A-Za-z_][A-Za-z0-9_]*)(?:\\s*=\\s*(.+))?")
}

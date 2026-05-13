package io.runescript.plugin.ide.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionUtil
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.Document
import com.intellij.openapi.module.ModuleUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.stubs.StubIndexKey
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import io.runescript.plugin.ide.completion.insertHandler.RsConstantInsertHandler
import io.runescript.plugin.ide.completion.insertHandler.RsKeywordSnippetInsertHandler
import io.runescript.plugin.ide.completion.insertHandler.RsOperatorInsertHandler
import io.runescript.plugin.ide.completion.insertHandler.RsScopedVariableInsertHandler
import io.runescript.plugin.ide.completion.insertHandler.RsScriptInsertHandler
import io.runescript.plugin.ide.completion.insertHandler.RsSymbolInsertHandler
import io.runescript.plugin.ide.completion.insertHandler.RsVariableInsertHandler
import io.runescript.plugin.ide.neptune.neptuneModuleData
import io.runescript.plugin.ide.neptune.typeManager
import io.runescript.plugin.lang.psi.RsArgumentList
import io.runescript.plugin.lang.psi.RsBlockStatement
import io.runescript.plugin.lang.psi.RsCommandExpression
import io.runescript.plugin.lang.psi.RsConstantExpression
import io.runescript.plugin.lang.psi.RsDynamicExpression
import io.runescript.plugin.lang.psi.RsGosubExpression
import io.runescript.plugin.lang.psi.RsHookFragment
import io.runescript.plugin.lang.psi.RsIfStatement
import io.runescript.plugin.lang.psi.RsLocalVariableDeclarationStatement
import io.runescript.plugin.lang.psi.RsLocalVariableExpression
import io.runescript.plugin.lang.psi.RsNameLiteral
import io.runescript.plugin.lang.psi.RsParameter
import io.runescript.plugin.lang.psi.RsReturnList
import io.runescript.plugin.lang.psi.RsScopedVariableExpression
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.RsStatement
import io.runescript.plugin.lang.psi.RsStatementList
import io.runescript.plugin.lang.psi.RsSwitchCase
import io.runescript.plugin.lang.psi.RsSwitchStatement
import io.runescript.plugin.lang.psi.RsTypeName
import io.runescript.plugin.lang.psi.isForArrayDeclaration
import io.runescript.plugin.lang.psi.isForVariableDeclaration
import io.runescript.plugin.lang.psi.scope.collectVariableDeclarations
import io.runescript.plugin.lang.psi.typechecker.trigger.ClientTriggerType
import io.runescript.plugin.lang.psi.typechecker.type.MetaType
import io.runescript.plugin.lang.psi.typechecker.type.PrimitiveType
import io.runescript.plugin.lang.psi.typechecker.type.ScriptVarType
import io.runescript.plugin.lang.psi.typechecker.type.TupleType
import io.runescript.plugin.lang.psi.typechecker.type.Type
import io.runescript.plugin.lang.psi.typechecker.type.TypeManager
import io.runescript.plugin.lang.psi.typechecker.type.wrapped.GameVarType
import io.runescript.plugin.lang.psi.typechecker.typeCheckedType
import io.runescript.plugin.lang.stubs.index.RsClientScriptIndex
import io.runescript.plugin.lang.stubs.index.RsCommandScriptIndex
import io.runescript.plugin.lang.stubs.index.RsProcScriptIndex
import io.runescript.plugin.symbollang.psi.RsSymSymbol
import io.runescript.plugin.symbollang.psi.index.RsSymbolIndex
import io.runescript.plugin.symbollang.psi.index.RsSymbolTypeIndex
import io.runescript.plugin.symbollang.psi.rawSymToType
import io.runescript.plugin.symbollang.psi.resolveToSymTypeName

class RsCompletionProvider : RsCompletionProviderBase() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet,
    ) {
        val position = parameters.position
        val request = RsCompletionContext.detect(parameters)
        val prefixedResult = result.withPrefixMatcher(request.prefix)
        val hasConstantMarkerPrefix = hasConstantMarkerPrefix(parameters, request.prefix)
        when (request.intent) {
            RsCompletionIntent.None -> {
                return
            }

            RsCompletionIntent.ScriptTrigger -> {
                addScriptTriggers(prefixedResult)
            }

            RsCompletionIntent.TypeName -> {
                addTypeNames(position, prefixedResult)
            }

            RsCompletionIntent.DefineType -> {
                addDefineTypes(position, prefixedResult)
            }

            RsCompletionIntent.SwitchType -> {
                addSwitchTypes(position, prefixedResult)
            }

            RsCompletionIntent.SwitchCase -> {
                addKeyword(
                    prefixedResult,
                    "case",
                    "switch",
                    priorityForPrefix("case", request.prefix, STATEMENT_KEYWORD_PRIORITY),
                    RsKeywordSnippetInsertHandler("case ${RsKeywordSnippetInsertHandler.CARET_MARKER}:"),
                )
            }

            RsCompletionIntent.SwitchCaseExpression -> {
                addKeyword(prefixedResult, "default", "switch", STATEMENT_KEYWORD_PRIORITY)
                addExpressionCompletions(
                    position,
                    parameters.offset,
                    request.expectedType,
                    request.prefix,
                    prefixedResult,
                    hasConstantMarkerPrefix,
                )
            }

            RsCompletionIntent.Statement -> {
                if (hasConstantMarkerPrefix) {
                    addConstants(position, prefixedResult, null, request.prefix, CONSTANT_PRIORITY)
                    return
                }
                addStatementKeywords(position, request, prefixedResult)
                addVariables(position, parameters.offset, prefixedResult, null, request.prefix, VARIABLE_PRIORITY)
                addScopedVariables(position, prefixedResult, null, request.prefix, SCOPED_VARIABLE_PRIORITY, includeIncompatible = true)
                addScripts(position, prefixedResult, RsCommandScriptIndex.KEY, null, null, request.prefix, COMMAND_PRIORITY)
                addScripts(position, prefixedResult, RsProcScriptIndex.KEY, "~", null, request.prefix, PROC_PRIORITY)
            }

            RsCompletionIntent.Expression -> {
                addExpressionCompletions(
                    position,
                    parameters.offset,
                    request.expectedType,
                    request.prefix,
                    prefixedResult,
                    hasConstantMarkerPrefix,
                )
            }

            RsCompletionIntent.ConditionOperator -> {
                addConditionOperators(prefixedResult)
            }

            RsCompletionIntent.Constant -> {
                addConstants(position, prefixedResult, request.expectedType, request.prefix, CONSTANT_REFERENCE_PRIORITY)
            }

            RsCompletionIntent.ScopedVariable -> {
                addScopedVariables(
                    position,
                    prefixedResult,
                    request.expectedType,
                    request.prefix,
                    SCOPED_VARIABLE_REFERENCE_PRIORITY,
                    includeIncompatible = true,
                )
            }

            RsCompletionIntent.LocalVariable -> {
                addVariables(position, parameters.offset, prefixedResult, request.expectedType, request.prefix, LOCAL_VARIABLE_PRIORITY)
            }

            RsCompletionIntent.CommandScript -> {
                addScripts(
                    position,
                    prefixedResult,
                    RsCommandScriptIndex.KEY,
                    null,
                    request.expectedType,
                    request.prefix,
                    SCRIPT_REFERENCE_PRIORITY,
                )
            }

            RsCompletionIntent.ProcScript -> {
                addScripts(
                    position,
                    prefixedResult,
                    RsProcScriptIndex.KEY,
                    "~",
                    request.expectedType,
                    request.prefix,
                    SCRIPT_REFERENCE_PRIORITY,
                )
            }

            RsCompletionIntent.ClientScript -> {
                addScripts(
                    position,
                    prefixedResult,
                    RsClientScriptIndex.KEY,
                    null,
                    request.expectedType,
                    request.prefix,
                    SCRIPT_REFERENCE_PRIORITY,
                )
            }
        }
    }

    private fun addStatementKeywords(
        position: PsiElement,
        request: RsCompletionRequest,
        result: CompletionResultSet,
    ) {
        addKeyword(
            result,
            "if",
            "statement",
            priorityForPrefix("if", request.prefix, STATEMENT_KEYWORD_PRIORITY),
            RsKeywordSnippetInsertHandler("if (${RsKeywordSnippetInsertHandler.CARET_MARKER}) {\n}"),
        )
        addKeyword(
            result,
            "while",
            "loop",
            priorityForPrefix("while", request.prefix, STATEMENT_KEYWORD_PRIORITY),
            RsKeywordSnippetInsertHandler("while (${RsKeywordSnippetInsertHandler.CARET_MARKER}) {\n}"),
        )
        addKeyword(result, "switch", "statement", priorityForPrefix("switch", request.prefix, STATEMENT_KEYWORD_PRIORITY))
        addKeyword(
            result,
            "return",
            "statement",
            priorityForPrefix("return", request.prefix, STATEMENT_KEYWORD_PRIORITY),
            returnInsertHandler(position),
        )
        addKeyword(result, "calc", "expression", priorityForPrefix("calc", request.prefix, EXPRESSION_KEYWORD_PRIORITY))

        for (keyword in position.typeManager.defineKeywords) {
            val keywordText = keyword.toString()
            if (!matchesPrefix(keywordText, request.prefix)) {
                continue
            }
            addKeyword(
                result,
                keywordText,
                "declaration",
                priorityForPrefix(keywordText, request.prefix, DECLARATION_PRIORITY),
                RsKeywordSnippetInsertHandler("$keywordText $${RsKeywordSnippetInsertHandler.CARET_MARKER} = ;"),
            )
        }
        for (keyword in position.typeManager.switchKeywords) {
            val keywordText = keyword.toString()
            if (!matchesPrefix(keywordText, request.prefix)) {
                continue
            }
            addKeyword(
                result,
                keywordText,
                "switch",
                priorityForPrefix(keywordText, request.prefix, DECLARATION_PRIORITY),
                RsKeywordSnippetInsertHandler("$keywordText (${RsKeywordSnippetInsertHandler.CARET_MARKER}) {\n}"),
            )
        }
    }

    private fun addExpressionCompletions(
        position: PsiElement,
        offset: Int,
        expectedType: Type?,
        prefix: String,
        result: CompletionResultSet,
        hasConstantMarkerPrefix: Boolean,
    ) {
        if (hasConstantMarkerPrefix) {
            addConstants(position, result, expectedType, prefix, CONSTANT_PRIORITY)
            return
        }
        val booleanPriority = priorityForType(position, expectedType, PrimitiveType.BOOLEAN, EXPRESSION_KEYWORD_PRIORITY)
        val genericExpressionGroup = genericExpressionGroup(expectedType)
        addKeyword(result, "true", "boolean", booleanPriority, grouping = genericExpressionGroup)
        addKeyword(result, "false", "boolean", booleanPriority, grouping = genericExpressionGroup)
        addKeyword(result, "null", "literal", nullPriority(expectedType), grouping = genericExpressionGroup)
        addKeyword(result, "calc", "expression", calcPriority(expectedType), grouping = genericExpressionGroup)
        addVariables(position, offset, result, expectedType, prefix, VARIABLE_PRIORITY)
        addScopedVariables(
            position,
            result,
            expectedType,
            prefix,
            SCOPED_VARIABLE_PRIORITY,
            includeIncompatible = expectedType == null,
        )
        addSymbols(position, result, expectedType, prefix)
        addScripts(position, result, RsCommandScriptIndex.KEY, null, expectedType, prefix, COMMAND_PRIORITY)
        addScripts(position, result, RsProcScriptIndex.KEY, "~", expectedType, prefix, PROC_PRIORITY)
    }

    private fun addConditionOperators(result: CompletionResultSet) {
        for (operator in CONDITION_OPERATORS) {
            val element =
                LookupElementBuilder
                    .create(operator)
                    .withTypeText("compare operator")
                    .withInsertHandler(RsOperatorInsertHandler)
            result.addPrioritizedElement(element, OPERATOR_PRIORITY)
        }
    }

    private fun hasConstantMarkerPrefix(
        parameters: CompletionParameters,
        prefix: String,
    ): Boolean {
        val markerOffset = parameters.offset - prefix.length - 1
        if (markerOffset < 0) {
            return false
        }
        return parameters.editor.document.charsSequence[markerOffset] == '^'
    }

    private fun addTypeNames(
        position: PsiElement,
        result: CompletionResultSet,
    ) {
        for (keyword in position.typeManager.typeKeywords) {
            addKeyword(result, keyword.toString(), "type", TYPE_PRIORITY)
        }
    }

    private fun addDefineTypes(
        position: PsiElement,
        result: CompletionResultSet,
    ) {
        for (keyword in position.typeManager.defineKeywords) {
            addKeyword(result, keyword.toString(), "declaration", TYPE_PRIORITY)
        }
    }

    private fun addSwitchTypes(
        position: PsiElement,
        result: CompletionResultSet,
    ) {
        for (keyword in position.typeManager.switchKeywords) {
            val keywordText = keyword.toString()
            addKeyword(
                result,
                keywordText,
                "switch",
                TYPE_PRIORITY,
                RsKeywordSnippetInsertHandler("$keywordText (${RsKeywordSnippetInsertHandler.CARET_MARKER}) {\n}"),
            )
        }
    }

    private fun addScriptTriggers(result: CompletionResultSet) {
        addKeyword(result, "command", "trigger", TRIGGER_PRIORITY)
        addKeyword(result, "proc", "trigger", TRIGGER_PRIORITY)
        addKeyword(result, "label", "trigger", TRIGGER_PRIORITY)
        for (trigger in ClientTriggerType.entries) {
            addKeyword(result, trigger.identifier, "trigger", TRIGGER_PRIORITY)
        }
    }

    private fun addVariables(
        position: PsiElement,
        offset: Int,
        result: CompletionResultSet,
        expectedType: Type?,
        prefix: String,
        priority: Double,
    ) {
        val declarations = position.collectVisibleVariableDeclarations()
        if (declarations.isEmpty()) {
            addTextVariableDeclarations(position, offset, result, expectedType, prefix, priority)
            return
        }
        for (declaration in declarations.distinctBy { it.name }) {
            val name = declaration.name ?: continue
            val lookupString = "$$name"
            if (!matchesPrefix(lookupString, prefix)) {
                continue
            }
            val type = declaration.safeTypeCheckedType()
            val element =
                LookupElementBuilder
                    .create(declaration, lookupString)
                    .withPresentableText(lookupString)
                    .withTypeText(type?.representation)
                    .withIcon(AllIcons.Nodes.Variable)
                    .withInsertHandler(RsVariableInsertHandler)
            addPrioritizedElement(
                result,
                result,
                lookupString,
                prefix,
                element,
                priorityForCandidate(position, expectedType, type, lookupString, prefix, priority),
            )
        }
    }

    private fun addTextVariableDeclarations(
        position: PsiElement,
        offset: Int,
        result: CompletionResultSet,
        expectedType: Type?,
        prefix: String,
        priority: Double,
    ) {
        val text = position.containingFile.text
        val textBeforeCaret = text.substring(0, offset.coerceIn(0, text.length))
        val seen = mutableSetOf<String>()
        for (match in LOCAL_DECLARATION_REGEX.findAll(textBeforeCaret)) {
            val defineType = match.groupValues[1]
            val variable = match.groupValues[2]
            if (!seen.add(variable)) {
                continue
            }
            if (!matchesPrefix(variable, prefix)) {
                continue
            }
            val type = position.typeManager.findOrNull(defineType.removePrefix("def_"), allowArray = true)
            val element =
                LookupElementBuilder
                    .create(variable)
                    .withPresentableText(variable)
                    .withTypeText(type?.representation ?: defineType.removePrefix("def_"))
                    .withIcon(AllIcons.Nodes.Variable)
                    .withInsertHandler(RsVariableInsertHandler)
            addPrioritizedElement(
                result,
                result,
                variable,
                prefix,
                element,
                priorityForCandidate(position, expectedType, type, variable, prefix, priority),
            )
        }
    }

    private fun PsiElement.collectVisibleVariableDeclarations(): List<RsLocalVariableExpression> {
        val scopedDeclarations = collectVariableDeclarations()
        if (scopedDeclarations.isNotEmpty()) {
            return scopedDeclarations
        }
        val offset = textOffset
        return PsiTreeUtil
            .findChildrenOfType(containingFile, RsLocalVariableExpression::class.java)
            .filter {
                it.textOffset < offset &&
                    (it.isForVariableDeclaration() || it.isForArrayDeclaration())
            }
    }

    private fun PsiElement.completionSearchScope(): GlobalSearchScope {
        val module = ModuleUtil.findModuleForPsiElement(this)
        return if (module != null) {
            GlobalSearchScope.moduleScope(module)
        } else {
            GlobalSearchScope.projectScope(project)
        }
    }

    private fun addConstants(
        position: PsiElement,
        result: CompletionResultSet,
        expectedType: Type?,
        prefix: String,
        priority: Double,
    ) {
        val project = position.project
        val scope = position.completionSearchScope()
        val cache = RsCompletionIndexCache.get(project)
        for (key in cache.matchingSymbolKeys(prefix)) {
            val keyLookupString = "^$key"
            if (!matchesPrefix(keyLookupString, prefix)) {
                continue
            }
            val symbols = StubIndex.getElements(RsSymbolIndex.KEY, key, project, scope, RsSymSymbol::class.java)
            for (symbol in symbols) {
                if (resolveToSymTypeName(symbol.containingFile) != "constant") {
                    continue
                }
                val name = symbol.name ?: continue
                val lookupString = "^$name"
                if (!matchesPrefix(lookupString, prefix)) {
                    continue
                }
                val type = symbol.constantCompletionType(position.typeManager)
                val element =
                    LookupElementBuilder
                        .create(symbol, lookupString)
                        .withPresentableText(lookupString)
                        .withTypeText(type?.representation ?: "constant")
                        .withIcon(AllIcons.Nodes.Constant)
                        .withInsertHandler(RsConstantInsertHandler)
                addPrioritizedElement(
                    result,
                    result,
                    lookupString,
                    prefix,
                    element,
                    priorityForConstantCandidate(position, expectedType, type, lookupString, prefix, priority),
                    completionGrouping = macroCompletionGroup(expectedType),
                )
            }
        }
    }

    private fun priorityForConstantCandidate(
        position: PsiElement,
        expectedType: Type?,
        actualType: Type?,
        lookupString: String,
        prefix: String,
        basePriority: Double,
    ): Double {
        val adjustedPriority =
            if (!prefix.startsWith("^") && expectedType?.symbolFileTypeName() != null) {
                TYPED_CONTEXT_CONSTANT_PRIORITY
            } else {
                basePriority
            }
        return priorityForCandidate(position, expectedType, actualType, lookupString, prefix, adjustedPriority)
    }

    private fun RsSymSymbol.constantCompletionType(typeManager: TypeManager): Type? =
        fieldList
            .getOrNull(1)
            ?.text
            ?.let { typeManager.findOrNull(it, allowArray = true) }

    private fun addScopedVariables(
        position: PsiElement,
        result: CompletionResultSet,
        expectedType: Type?,
        prefix: String,
        priority: Double,
        includeIncompatible: Boolean,
    ) {
        val moduleData = position.neptuneModuleData ?: return
        val project = position.project
        val scope = position.completionSearchScope()
        val cache = RsCompletionIndexCache.get(project)

        fun addScopedVariable(symbol: RsSymSymbol): Boolean {
            val gameVarType =
                rawSymToType(symbol, moduleData.resolvedData.types, moduleData.resolvedData.symbolLoaders) as? GameVarType
                    ?: return false
            val name = symbol.name ?: return false
            val lookupString = "%$name"
            if (!matchesPrefix(lookupString, prefix)) {
                return false
            }
            val element =
                LookupElementBuilder
                    .create(symbol, lookupString)
                    .withPresentableText(lookupString)
                    .withTypeText(gameVarType.inner.representation)
                    .withIcon(AllIcons.Nodes.Variable)
                    .withInsertHandler(RsScopedVariableInsertHandler)
            addPrioritizedElement(
                result,
                result,
                lookupString,
                prefix,
                element,
                priorityForCandidate(position, expectedType, gameVarType.inner, lookupString, prefix, priority),
            )
            return true
        }

        fun addTypedScopedVariables(): Boolean {
            val typeName = expectedType?.scopedVariableTypeName() ?: return false
            val typePrefix = RsSymbolTypeIndex.scopedVarTypePrefix(typeName)
            var added = false
            for (key in cache.matchingTypedSymbolKeys(typePrefix, prefix)) {
                val name = key.removePrefix(typePrefix)
                val lookupString = "%$name"
                if (!matchesPrefix(lookupString, prefix)) {
                    continue
                }
                val symbols = StubIndex.getElements(RsSymbolTypeIndex.KEY, key, project, scope, RsSymSymbol::class.java)
                for (symbol in symbols) {
                    added = addScopedVariable(symbol) || added
                }
            }
            return added
        }

        fun addMatchingScopedVariables(compatibleWithExpectedType: Boolean?) {
            for (key in cache.matchingSymbolKeys(prefix)) {
                val keyLookupString = "%$key"
                if (!matchesPrefix(keyLookupString, prefix)) {
                    continue
                }
                val symbols = StubIndex.getElements(RsSymbolIndex.KEY, key, project, scope, RsSymSymbol::class.java)
                for (symbol in symbols) {
                    val gameVarType =
                        rawSymToType(symbol, moduleData.resolvedData.types, moduleData.resolvedData.symbolLoaders) as? GameVarType
                            ?: continue
                    if (compatibleWithExpectedType != null) {
                        val isCompatible =
                            expectedType != null &&
                                position.typeManager.isCompletionCompatible(
                                    expectedType,
                                    gameVarType.inner,
                                )
                        if (isCompatible != compatibleWithExpectedType) {
                            continue
                        }
                    }
                    addScopedVariable(symbol)
                }
            }
        }

        if (expectedType == null) {
            addMatchingScopedVariables(compatibleWithExpectedType = null)
            return
        }

        val addedTypedScopedVariables = addTypedScopedVariables()
        if (!addedTypedScopedVariables) {
            addMatchingScopedVariables(compatibleWithExpectedType = true)
        }
        if (includeIncompatible) {
            addMatchingScopedVariables(compatibleWithExpectedType = false)
        }
    }

    private fun addSymbols(
        position: PsiElement,
        result: CompletionResultSet,
        expectedType: Type?,
        prefix: String,
    ) {
        if (expectedType == null) {
            return
        }
        if (addTypedSymbolIndexCompletions(position, result, expectedType, prefix)) {
            return
        }
        val moduleData = position.neptuneModuleData ?: return
        val project = position.project
        val scope = position.completionSearchScope()
        val cache = RsCompletionIndexCache.get(project)
        for (key in cache.matchingSymbolKeys(prefix)) {
            if (!matchesPrefix(key, prefix)) {
                continue
            }
            val symbols = StubIndex.getElements(RsSymbolIndex.KEY, key, project, scope, RsSymSymbol::class.java)
            for (symbol in symbols) {
                if (resolveToSymTypeName(symbol.containingFile) == "constant") {
                    continue
                }
                val type =
                    runCatching {
                        rawSymToType(symbol, moduleData.resolvedData.types, moduleData.resolvedData.symbolLoaders)
                    }.getOrNull()
                if (type == null || type is GameVarType || type == MetaType.Error) {
                    continue
                }
                if (!position.typeManager.isCompatible(expectedType, type)) {
                    continue
                }
                val name = symbol.name ?: continue
                if (!matchesPrefix(name, prefix)) {
                    continue
                }
                val element =
                    LookupElementBuilder
                        .create(symbol, name)
                        .withTypeText(type.representation)
                        .withIcon(AllIcons.Nodes.Property)
                        .withInsertHandler(RsSymbolInsertHandler)
                result.addPrioritizedElement(element, priorityForCandidate(position, expectedType, type, name, prefix, SYMBOL_PRIORITY))
            }
        }
    }

    private fun addTypedSymbolIndexCompletions(
        position: PsiElement,
        result: CompletionResultSet,
        expectedType: Type,
        prefix: String,
    ): Boolean {
        val symbolType = expectedType.symbolFileTypeName() ?: return false
        val project = position.project
        val scope = position.completionSearchScope()
        val typePrefix = RsSymbolTypeIndex.typePrefix(symbolType)
        var added = 0
        val cache = RsCompletionIndexCache.get(project)
        for (key in cache.typedSymbolKeys(typePrefix)) {
            if (!key.startsWith(typePrefix)) {
                continue
            }
            val name = key.removePrefix(typePrefix)
            if (!matchesPrefix(name, prefix)) {
                continue
            }
            val symbols = StubIndex.getElements(RsSymbolTypeIndex.KEY, key, project, scope, RsSymSymbol::class.java)
            for (symbol in symbols) {
                val actualType = if (symbolType == "obj") ScriptVarType.NAMEDOBJ else expectedType
                val element =
                    LookupElementBuilder
                        .create(symbol, name)
                        .withTypeText(actualType.representation)
                        .withIcon(AllIcons.Nodes.Property)
                        .withInsertHandler(RsSymbolInsertHandler)
                result.addPrioritizedElement(
                    element,
                    priorityForCandidate(position, expectedType, actualType, name, prefix, SYMBOL_PRIORITY),
                )
                added++
                if (added >= MAX_SYMBOL_INDEX_COMPLETIONS) {
                    return true
                }
            }
        }
        return added > 0
    }

    private fun Type.symbolFileTypeName(): String? =
        when (this) {
            ScriptVarType.NAMEDOBJ -> "obj"
            is ScriptVarType -> representation
            else -> null
        }

    private fun Type.scopedVariableTypeName(): String? =
        when (this) {
            ScriptVarType.NAMEDOBJ -> "obj"
            else -> representation
        }

    private fun addScripts(
        position: PsiElement,
        result: CompletionResultSet,
        indexKey: StubIndexKey<String, RsScript>,
        insertPrefix: String?,
        expectedType: Type?,
        prefix: String,
        priority: Double,
    ) {
        val project = position.project
        val scope = position.completionSearchScope()
        val cache = RsCompletionIndexCache.get(project)
        for (key in cache.matchingScriptKeys(indexKey, prefix)) {
            if (!matchesPrefix(key, prefix)) {
                continue
            }
            val scripts = StubIndex.getElements(indexKey, key, project, scope, RsScript::class.java)
            for (script in scripts) {
                val name = script.name ?: continue
                if (!matchesPrefix(name, prefix)) {
                    continue
                }
                val returnType = script.returnCompletionType()
                addPrioritizedElement(
                    result,
                    result,
                    name,
                    prefix,
                    createScriptLookupElement(script, insertPrefix),
                    priorityForCandidate(position, expectedType, returnType, name, prefix, priority),
                )
            }
        }
    }

    private fun createScriptLookupElement(
        script: RsScript,
        insertPrefix: String?,
    ): LookupElement {
        val typeText = script.returnList?.typeNameList?.joinToString(",") { it.text } ?: "unit"
        val presentationText =
            buildString {
                append(script.name)
                val parameters = script.parameterList?.parameterList.orEmpty()
                if (parameters.isNotEmpty()) {
                    append('(')
                    parameters.forEachIndexed { index, parameter ->
                        if (index > 0) append(", ")
                        append(parameter.typeName.text)
                        append(' ')
                        append(parameter.localVariableExpression?.name ?: "???")
                    }
                    append(')')
                }
            }
        return LookupElementBuilder
            .create(script, script.name ?: "")
            .withTypeText(typeText)
            .withPresentableText(presentationText)
            .withIcon(script.getIcon(0))
            .withInsertHandler(RsScriptInsertHandler(insertPrefix))
    }

    private fun addKeyword(
        result: CompletionResultSet,
        keyword: String,
        typeText: String,
        priority: Double,
        insertHandler: com.intellij.codeInsight.completion.InsertHandler<LookupElement>? = null,
        grouping: Int = STRONG_COMPLETION_GROUP,
    ) {
        val element =
            LookupElementBuilder
                .create(keyword)
                .withBoldness(true)
                .withTypeText(typeText)
                .let { if (insertHandler != null) it.withInsertHandler(insertHandler) else it }
        result.addPrioritizedElement(element, priority, grouping)
    }

    private fun priorityForType(
        position: PsiElement,
        expectedType: Type?,
        actualType: Type?,
        basePriority: Double,
    ): Double {
        if (expectedType == null || actualType == null) {
            return basePriority
        }
        return if (position.typeManager.isCompletionCompatible(expectedType, actualType)) {
            basePriority + EXPECTED_TYPE_PRIORITY_BOOST
        } else {
            basePriority + UNEXPECTED_TYPE_PRIORITY_PENALTY
        }
    }

    private fun nullPriority(expectedType: Type?): Double =
        when (expectedType) {
            null, MetaType.Any -> {
                EXPRESSION_KEYWORD_PRIORITY
            }

            PrimitiveType.BOOLEAN, PrimitiveType.INT, PrimitiveType.LONG, PrimitiveType.CHAR, PrimitiveType.COORD -> {
                EXPRESSION_KEYWORD_PRIORITY + UNEXPECTED_TYPE_PRIORITY_PENALTY
            }

            else -> {
                EXPRESSION_KEYWORD_PRIORITY + EXPECTED_TYPE_PRIORITY_BOOST
            }
        }

    private fun calcPriority(expectedType: Type?): Double =
        when (expectedType) {
            null, MetaType.Any, PrimitiveType.INT, PrimitiveType.LONG -> EXPRESSION_KEYWORD_PRIORITY
            else -> EXPRESSION_KEYWORD_PRIORITY + UNEXPECTED_TYPE_PRIORITY_PENALTY
        }

    private fun genericExpressionGroup(expectedType: Type?): Int =
        if (expectedType?.symbolFileTypeName() != null) {
            GENERIC_EXPRESSION_COMPLETION_GROUP
        } else {
            STRONG_COMPLETION_GROUP
        }

    private fun macroCompletionGroup(expectedType: Type?): Int =
        if (expectedType?.symbolFileTypeName() != null) {
            MACRO_COMPLETION_GROUP
        } else {
            STRONG_COMPLETION_GROUP
        }

    private fun priorityForCandidate(
        position: PsiElement,
        expectedType: Type?,
        actualType: Type?,
        lookupString: String,
        prefix: String,
        basePriority: Double,
    ): Double =
        priorityForPrefix(
            lookupString,
            prefix,
            priorityForType(position, expectedType, actualType, basePriority),
        )

    private fun priorityForPrefix(
        lookupString: String,
        prefix: String,
        basePriority: Double,
    ): Double {
        val normalizedPrefix = normalizePrefix(prefix)
        val normalizedLookup = normalizePrefix(lookupString)
        if (normalizedPrefix.isBlank()) {
            return basePriority
        }
        return when {
            normalizedLookup.equals(normalizedPrefix, ignoreCase = true) -> basePriority + EXACT_PREFIX_PRIORITY_BOOST
            normalizedLookup.startsWith(normalizedPrefix, ignoreCase = true) -> basePriority + PREFIX_PRIORITY_BOOST
            normalizedLookup.contains(normalizedPrefix, ignoreCase = true) -> basePriority + SUBSTRING_PREFIX_PRIORITY_BOOST
            fuzzyMatches(normalizedLookup, normalizedPrefix) -> basePriority + FUZZY_PREFIX_PRIORITY_BOOST
            else -> basePriority
        }
    }

    private fun matchesPrefix(
        lookupString: String,
        prefix: String,
    ): Boolean {
        val normalizedPrefix = normalizePrefix(prefix)
        if (normalizedPrefix.isBlank()) {
            return true
        }
        val normalizedLookup = normalizePrefix(lookupString)
        return normalizedLookup.startsWith(normalizedPrefix, ignoreCase = true) ||
            normalizedLookup.contains(normalizedPrefix, ignoreCase = true) ||
            fuzzyMatches(normalizedLookup, normalizedPrefix)
    }

    private fun isPlainPrefixMatch(
        lookupString: String,
        prefix: String,
    ): Boolean {
        val normalizedPrefix = normalizePrefix(prefix)
        return normalizedPrefix.isBlank() ||
            normalizePrefix(lookupString).startsWith(normalizedPrefix, ignoreCase = true)
    }

    private fun addPrioritizedElement(
        result: CompletionResultSet,
        looseResult: CompletionResultSet,
        lookupString: String,
        prefix: String,
        element: LookupElement,
        priority: Double,
        completionGrouping: Int = STRONG_COMPLETION_GROUP,
    ) {
        val plainPrefixMatch = isPlainPrefixMatch(lookupString, prefix)
        val targetResult =
            if (plainPrefixMatch) {
                result
            } else {
                looseResult
            }
        targetResult.addPrioritizedElement(element, priority, completionGrouping)
    }

    private fun normalizePrefix(text: String): String =
        text
            .substringBefore(CompletionUtil.DUMMY_IDENTIFIER_TRIMMED)
            .removePrefix("$")
            .removePrefix("~")
            .removePrefix("^")
            .removePrefix("%")

    private fun fuzzyMatches(
        lookupString: String,
        prefix: String,
    ): Boolean {
        var lookupIndex = 0
        for (prefixChar in prefix) {
            lookupIndex = lookupString.indexOf(prefixChar, lookupIndex, ignoreCase = true)
            if (lookupIndex < 0) {
                return false
            }
            lookupIndex++
        }
        return true
    }

    private fun returnInsertHandler(position: PsiElement): RsKeywordSnippetInsertHandler {
        val script = position.parentOfType<RsScript>()
        val hasReturnValues =
            script
                ?.returnList
                ?.typeNameList
                .orEmpty()
                .isNotEmpty()
        val snippet =
            if (hasReturnValues) {
                "return(${RsKeywordSnippetInsertHandler.CARET_MARKER});"
            } else {
                "return;"
            }
        return RsKeywordSnippetInsertHandler(snippet)
    }

    private fun TypeManager.isCompatible(
        expectedType: Type,
        actualType: Type,
    ): Boolean =
        expectedType == actualType ||
            expectedType == MetaType.Any ||
            actualType == MetaType.Error ||
            check(expectedType, actualType)

    private fun TypeManager.isCompletionCompatible(
        expectedType: Type,
        actualType: Type,
    ): Boolean =
        isCompatible(expectedType, actualType) ||
            (expectedType == ScriptVarType.OBJ && actualType == ScriptVarType.NAMEDOBJ)

    private fun RsLocalVariableExpression.safeTypeCheckedType(): Type? = runCatching { typeCheckedType }.getOrNull()

    private fun RsScript.returnCompletionType(): Type? = containingFile.typeManager.typeFromTypeNames(returnList?.typeNameList.orEmpty())

    private fun TypeManager.typeFromTypeNames(typeNames: List<RsTypeName>): Type? {
        val types = typeNames.mapNotNull { findOrNull(it.text, allowArray = true) }
        if (types.size != typeNames.size) {
            return null
        }
        return TupleType.fromList(types)
    }

    private fun CompletionResultSet.addPrioritizedElement(
        element: LookupElement,
        priority: Double,
        grouping: Int = STRONG_COMPLETION_GROUP,
    ) {
        val groupedElement =
            if (grouping == STRONG_COMPLETION_GROUP) {
                element
            } else {
                PrioritizedLookupElement.withGrouping(element, grouping)
            }
        addElement(PrioritizedLookupElement.withPriority(groupedElement, priority))
    }

    private data class RsCompletionRequest(
        val intent: RsCompletionIntent,
        val expectedType: Type? = null,
        val prefix: String = "",
    )

    private enum class RsCompletionIntent {
        None,
        Statement,
        Expression,
        ConditionOperator,
        Constant,
        ScopedVariable,
        LocalVariable,
        CommandScript,
        ProcScript,
        ClientScript,
        ScriptTrigger,
        TypeName,
        DefineType,
        SwitchType,
        SwitchCase,
        SwitchCaseExpression,
    }

    private object RsCompletionContext {
        fun currentPrefix(
            text: String,
            offset: Int,
        ): String = currentWordPrefix(text, offset)

        fun detect(parameters: CompletionParameters): RsCompletionRequest {
            val position = parameters.position
            val namedParent = PsiTreeUtil.getParentOfType(position, RsNameLiteral::class.java, false)
            val target = namedParent ?: position
            val document = parameters.editor.document
            val offset = parameters.offset.coerceIn(0, document.textLength)
            val prefix = currentPrefix(document.text, offset)
            val request = detectKind(parameters, target, prefix)
            return request.copy(prefix = prefix)
        }

        private fun detectKind(
            parameters: CompletionParameters,
            position: PsiElement,
            prefix: String,
        ): RsCompletionRequest {
            val document = parameters.editor.document
            val offset = parameters.offset.coerceIn(0, document.textLength)
            if (isScriptTriggerPosition(document, offset)) {
                return RsCompletionRequest(RsCompletionIntent.ScriptTrigger)
            }
            if (isScriptSignatureTypePosition(document, offset)) {
                return RsCompletionRequest(RsCompletionIntent.TypeName)
            }
            if (isSwitchTypeTextPosition(document, offset)) {
                return RsCompletionRequest(RsCompletionIntent.SwitchType)
            }
            if (isSwitchCaseExpressionTextPosition(document, offset)) {
                return RsCompletionRequest(
                    RsCompletionIntent.SwitchCaseExpression,
                    expectedType = switchCaseExpectedType(position, document, offset),
                )
            }
            if (isSwitchCaseTextPosition(document, offset)) {
                return RsCompletionRequest(RsCompletionIntent.SwitchCase)
            }
            if (isStandaloneStatementPrefix(document, offset, prefix)) {
                return RsCompletionRequest(RsCompletionIntent.Statement)
            }
            if (isStatementTextPosition(document, offset)) {
                return RsCompletionRequest(RsCompletionIntent.Statement)
            }
            if (isConditionOperatorTextPosition(document, offset)) {
                return RsCompletionRequest(RsCompletionIntent.ConditionOperator)
            }
            if (position.parentOfType<RsTypeName>() != null ||
                position.parentOfType<RsParameter>() != null ||
                position.parentOfType<RsReturnList>() != null
            ) {
                return RsCompletionRequest(RsCompletionIntent.TypeName)
            }
            if (position.parentOfType<RsLocalVariableDeclarationStatement>() != null) {
                if (isDeclarationInitializerTextPosition(document, offset)) {
                    return RsCompletionRequest(
                        RsCompletionIntent.Expression,
                        expectedType = declarationInitializerExpectedType(position, document, offset),
                    )
                }
                return RsCompletionRequest(RsCompletionIntent.DefineType)
            }
            if (position.parentOfType<RsSwitchStatement>()?.switch === position) {
                return RsCompletionRequest(RsCompletionIntent.SwitchType)
            }
            if (position.parentOfType<RsLocalVariableExpression>() != null ||
                position.parentOfType<RsScopedVariableExpression>() != null
            ) {
                val localVariable = position.parentOfType<RsLocalVariableExpression>()
                return if (position.parentOfType<RsScopedVariableExpression>() != null) {
                    RsCompletionRequest(
                        RsCompletionIntent.ScopedVariable,
                        expectedType = expressionExpectedType(position, document, offset),
                    )
                } else if (localVariable?.isForVariableDeclaration() == true ||
                    localVariable?.isForArrayDeclaration() == true ||
                    position.parentOfType<RsScopedVariableExpression>() != null
                ) {
                    RsCompletionRequest(RsCompletionIntent.None)
                } else {
                    RsCompletionRequest(
                        RsCompletionIntent.LocalVariable,
                        expectedType = expressionExpectedType(position, document, offset),
                    )
                }
            }
            if (position.parentOfType<RsArgumentList>() != null) {
                return RsCompletionRequest(
                    RsCompletionIntent.Expression,
                    expectedType = argumentExpectedType(position, document, offset),
                )
            }
            if (position.parentOfType<RsGosubExpression>() != null) {
                return RsCompletionRequest(RsCompletionIntent.ProcScript)
            }
            if (position.parentOfType<RsHookFragment>() != null) {
                return RsCompletionRequest(RsCompletionIntent.ClientScript)
            }
            if (position.parentOfType<RsCommandExpression>() != null) {
                return RsCompletionRequest(RsCompletionIntent.CommandScript)
            }
            if (position.parentOfType<RsConstantExpression>() != null) {
                return RsCompletionRequest(
                    RsCompletionIntent.Constant,
                    expectedType = expressionExpectedType(position, document, offset),
                )
            }
            if (position.parentOfType<RsDynamicExpression>() != null ||
                position.parentOfType<RsIfStatement>() != null ||
                position.parentOfType<RsSwitchCase>() != null
            ) {
                return RsCompletionRequest(
                    RsCompletionIntent.Expression,
                    expectedType = expressionExpectedType(position, document, offset),
                )
            }
            if (currentCall(document.text, offset) != null) {
                return RsCompletionRequest(
                    RsCompletionIntent.Expression,
                    expectedType = argumentExpectedType(position, document, offset),
                )
            }
            if (isStatementPosition(position)) {
                return RsCompletionRequest(RsCompletionIntent.Statement)
            }
            return RsCompletionRequest(
                RsCompletionIntent.Expression,
                expectedType = expressionExpectedType(position, document, offset),
            )
        }

        private fun isScriptTriggerPosition(
            document: Document,
            offset: Int,
        ): Boolean {
            val lineStart = document.getLineStartOffset(document.getLineNumber(offset))
            val prefix = document.text.substring(lineStart, offset)
            val bracketIndex = prefix.lastIndexOf('[')
            if (bracketIndex < 0) {
                return false
            }
            return ',' !in prefix.substring(bracketIndex + 1)
        }

        private fun isScriptSignatureTypePosition(
            document: Document,
            offset: Int,
        ): Boolean {
            val text = document.text
            val headerEnd = text.lastIndexOf(']', offset.coerceAtMost(text.length - 1))
            if (headerEnd !in 0 until offset) {
                return false
            }
            val headerLineEnd = firstLineBreakAfter(text, headerEnd)
            if (headerLineEnd != null && offset > headerLineEnd) {
                return false
            }
            val bodyStart = text.indexOf('{', headerEnd)
            if (bodyStart in 0 until offset) {
                return false
            }
            val previous = previousNonWhitespace(text, offset)
            return previous == '(' || previous == ','
        }

        private fun isStatementTextPosition(
            document: Document,
            offset: Int,
        ): Boolean =
            when (previousNonWhitespace(document.text, offset)) {
                '{', ';', '}' -> true
                else -> false
            }

        private fun isStandaloneStatementPrefix(
            document: Document,
            offset: Int,
            prefix: String,
        ): Boolean {
            if (prefix.isBlank()) {
                return false
            }
            val lineStart = document.getLineStartOffset(document.getLineNumber(offset))
            val prefixStart = (offset - prefix.length).coerceAtLeast(lineStart)
            return document.text.substring(lineStart, prefixStart).isBlank()
        }

        private fun isSwitchTypeTextPosition(
            document: Document,
            offset: Int,
        ): Boolean = currentWordPrefix(document.text, offset).startsWith("switch")

        private fun isSwitchCaseExpressionTextPosition(
            document: Document,
            offset: Int,
        ): Boolean {
            val prefix = currentLinePrefix(document, offset).trimStart()
            return prefix.startsWith("case ") && ':' !in prefix
        }

        private fun isSwitchCaseTextPosition(
            document: Document,
            offset: Int,
        ): Boolean {
            val prefix = currentLinePrefix(document, offset).trimStart()
            if (prefix.isNotEmpty() && !"case".startsWith(prefix)) {
                return false
            }
            return insideOpenSwitchBody(document.text, offset)
        }

        private fun isDeclarationInitializerTextPosition(
            document: Document,
            offset: Int,
        ): Boolean {
            val statementPrefix = currentStatementPrefix(document.text, offset)
            return LOCAL_DECLARATION_INITIALIZER_REGEX.containsMatchIn(statementPrefix)
        }

        private fun isConditionOperatorTextPosition(
            document: Document,
            offset: Int,
        ): Boolean {
            val previous = document.text.getOrNull(offset - 1)
            if (previous != null && !previous.isWhitespace()) {
                return false
            }
            val prefix = currentStatementPrefix(document.text, offset)
            val conditionStart = maxOf(prefix.lastIndexOf("if"), prefix.lastIndexOf("while"))
            if (conditionStart < 0) {
                return false
            }
            val openParen = prefix.indexOf('(', conditionStart)
            if (openParen < 0 || prefix.indexOf(')', openParen) >= 0) {
                return false
            }
            val conditionPrefix = prefix.substring(openParen + 1)
            if (CONDITION_OPERATOR_REGEX.containsMatchIn(conditionPrefix)) {
                return false
            }
            return CONDITION_OPERAND_REGEX.matches(conditionPrefix.trim())
        }

        private fun declarationInitializerExpectedType(
            position: PsiElement,
            document: Document,
            offset: Int,
        ): Type? {
            val statementPrefix = currentStatementPrefix(document.text, offset)
            val match = LOCAL_DECLARATION_INITIALIZER_REGEX.find(statementPrefix) ?: return null
            return position.typeManager.findOrNull(match.groupValues[1], allowArray = true)
        }

        private fun expressionExpectedType(
            position: PsiElement,
            document: Document,
            offset: Int,
        ): Type? =
            declarationInitializerExpectedType(position, document, offset)
                ?: assignmentExpectedType(position, document, offset)
                ?: conditionRightHandSideExpectedType(position, document, offset)
                ?: conditionStartExpectedType(document, offset)
                ?: returnExpectedType(position, document, offset)
                ?: switchSelectorExpectedType(position, document, offset)

        private fun assignmentExpectedType(
            position: PsiElement,
            document: Document,
            offset: Int,
        ): Type? {
            val statementPrefix = currentStatementPrefix(document.text, offset)
            val match = ASSIGNMENT_VALUE_REGEX.find(statementPrefix) ?: return null
            return variableType(position, match.groupValues[1])
        }

        private fun conditionRightHandSideExpectedType(
            position: PsiElement,
            document: Document,
            offset: Int,
        ): Type? {
            val statementPrefix = currentStatementPrefix(document.text, offset)
            val match = CONDITION_VALUE_REGEX.find(statementPrefix) ?: return null
            return variableType(position, match.groupValues[1])
        }

        private fun conditionStartExpectedType(
            document: Document,
            offset: Int,
        ): Type? {
            val statementPrefix = currentStatementPrefix(document.text, offset)
            val conditionStart = maxOf(statementPrefix.lastIndexOf("if"), statementPrefix.lastIndexOf("while"))
            if (conditionStart < 0) {
                return null
            }
            val openParen = statementPrefix.indexOf('(', conditionStart)
            if (openParen < 0 || statementPrefix.indexOf(')', openParen) >= 0) {
                return null
            }
            val conditionPrefix = statementPrefix.substring(openParen + 1)
            if (CONDITION_OPERATOR_REGEX.containsMatchIn(conditionPrefix)) {
                return null
            }
            return PrimitiveType.BOOLEAN
        }

        private fun returnExpectedType(
            position: PsiElement,
            document: Document,
            offset: Int,
        ): Type? {
            if (!RETURN_VALUE_REGEX.containsMatchIn(currentStatementPrefix(document.text, offset))) {
                return null
            }
            val script =
                position.parentOfType<RsScript>()
                    ?: return scriptReturnExpectedTypeFromText(position.typeManager, document.text, offset)
            val returnList = script.returnList
            if (returnList != null) {
                return typeFromTypeNames(position.typeManager, returnList.typeNameList)
            }
            return scriptReturnExpectedTypeFromText(position.typeManager, document.text, offset)
        }

        private fun scriptReturnExpectedTypeFromText(
            typeManager: TypeManager,
            text: String,
            offset: Int,
        ): Type? {
            val beforeCaret = text.substring(0, offset.coerceIn(0, text.length))
            val headerStart = beforeCaret.lastIndexOf('[')
            if (headerStart < 0) {
                return null
            }
            val bodyStart = text.indexOf('{', headerStart)
            if (bodyStart > offset) {
                return null
            }
            val headerEnd =
                if (bodyStart >= 0) {
                    bodyStart
                } else {
                    firstLineBreakAfter(text, headerStart)?.coerceAtMost(offset) ?: offset
                }
            val header = text.substring(headerStart, headerEnd)
            val signature = header.substringAfter(']', missingDelimiterValue = "")
            val parenthesizedGroups = PARENTHESIZED_GROUP_REGEX.findAll(signature).toList()
            if (parenthesizedGroups.size < 2) {
                return null
            }
            val returnTypes =
                parenthesizedGroups
                    .last()
                    .groupValues[1]
                    .split(',')
                    .mapNotNull { it.trim().takeIf(String::isNotEmpty) }
                    .mapNotNull { typeManager.findOrNull(it, allowArray = true) }
            return TupleType.fromList(returnTypes)
        }

        private fun switchSelectorExpectedType(
            position: PsiElement,
            document: Document,
            offset: Int,
        ): Type? {
            val match = SWITCH_SELECTOR_REGEX.find(currentStatementPrefix(document.text, offset)) ?: return null
            return position.typeManager.findOrNull(match.groupValues[1], allowArray = true)
        }

        private fun switchCaseExpectedType(
            position: PsiElement,
            document: Document,
            offset: Int,
        ): Type? {
            val beforeCaret = document.text.substring(0, offset.coerceIn(0, document.text.length))
            val match = SWITCH_CASE_TYPE_REGEX.findAll(beforeCaret).lastOrNull() ?: return null
            return position.typeManager.findOrNull(match.groupValues[1], allowArray = true)
        }

        private fun argumentExpectedType(
            position: PsiElement,
            document: Document,
            offset: Int,
        ): Type? {
            val call = currentCall(document.text, offset) ?: return expressionExpectedType(position, document, offset)
            if (call.name == "return") {
                return returnExpectedType(position, document, offset)
            }
            if (call.name.startsWith("switch_")) {
                return switchSelectorExpectedType(position, document, offset)
            }
            return scriptParameterTypes(position, call.name, call.isProc).getOrNull(call.argumentIndex)
        }

        private fun variableType(
            position: PsiElement,
            variableName: String,
        ): Type? {
            val normalizedName = variableName.removePrefix("$")
            val declaration =
                collectVisibleVariableDeclarations(position)
                    .lastOrNull { it.name == normalizedName }
            if (declaration != null) {
                return safeTypeCheckedType(declaration)
            }
            val prefix = position.containingFile.text.substring(0, position.textOffset.coerceIn(0, position.containingFile.textLength))
            val match =
                LOCAL_DECLARATION_REGEX
                    .findAll(prefix)
                    .lastOrNull { it.groupValues[2].removePrefix("$") == normalizedName }
                    ?: return null
            return position.typeManager.findOrNull(match.groupValues[1].removePrefix("def_"), allowArray = true)
        }

        private fun collectVisibleVariableDeclarations(position: PsiElement): List<RsLocalVariableExpression> {
            val scopedDeclarations = position.collectVariableDeclarations()
            if (scopedDeclarations.isNotEmpty()) {
                return scopedDeclarations
            }
            val offset = position.textOffset
            return PsiTreeUtil
                .findChildrenOfType(position.containingFile, RsLocalVariableExpression::class.java)
                .filter {
                    it.textOffset < offset &&
                        (it.isForVariableDeclaration() || it.isForArrayDeclaration())
                }
        }

        private fun safeTypeCheckedType(declaration: RsLocalVariableExpression): Type? =
            runCatching { declaration.typeCheckedType }.getOrNull()

        private fun typeFromTypeNames(
            typeManager: TypeManager,
            typeNames: List<RsTypeName>,
        ): Type? {
            val types = typeNames.mapNotNull { typeManager.findOrNull(it.text, allowArray = true) }
            if (types.size != typeNames.size) {
                return null
            }
            return TupleType.fromList(types)
        }

        private fun scriptParameterTypes(
            position: PsiElement,
            name: String,
            isProc: Boolean,
        ): List<Type> {
            val key = if (isProc) RsProcScriptIndex.KEY else RsCommandScriptIndex.KEY
            val scripts = lookupScripts(position, key, name)
            return scripts
                .firstOrNull()
                ?.parameterList
                ?.parameterList
                .orEmpty()
                .mapNotNull { position.typeManager.findOrNull(it.typeName.text, allowArray = true) }
        }

        private fun lookupScripts(
            position: PsiElement,
            indexKey: StubIndexKey<String, RsScript>,
            name: String,
        ): Collection<RsScript> {
            val project = position.project
            val scope = completionSearchScope(position)
            return StubIndex.getElements(indexKey, name, project, scope, RsScript::class.java)
        }

        private fun completionSearchScope(position: PsiElement): GlobalSearchScope {
            val module = ModuleUtil.findModuleForPsiElement(position)
            return if (module != null) {
                GlobalSearchScope.moduleScope(module)
            } else {
                GlobalSearchScope.projectScope(position.project)
            }
        }

        private fun currentCall(
            text: String,
            offset: Int,
        ): CallInfo? {
            val prefix = currentStatementPrefix(text, offset)
            val openParen = prefix.lastIndexOf('(')
            if (openParen < 0) {
                return null
            }
            val closeParen = prefix.indexOf(')', openParen)
            if (closeParen >= 0) {
                return null
            }
            var nameEnd = openParen
            while (nameEnd > 0 && prefix[nameEnd - 1].isWhitespace()) {
                nameEnd--
            }
            var nameStart = nameEnd
            while (nameStart > 0 && isIdentifierPart(prefix[nameStart - 1])) {
                nameStart--
            }
            if (nameStart == nameEnd) {
                return null
            }
            val isProc = nameStart > 0 && prefix[nameStart - 1] == '~'
            val argumentIndex = prefix.substring(openParen + 1).count { it == ',' }
            return CallInfo(prefix.substring(nameStart, nameEnd), isProc, argumentIndex)
        }

        private data class CallInfo(
            val name: String,
            val isProc: Boolean,
            val argumentIndex: Int,
        )

        private fun currentStatementPrefix(
            text: String,
            offset: Int,
        ): String {
            var index = (offset - 1).coerceAtMost(text.lastIndex)
            while (index >= 0) {
                when (text[index]) {
                    '{', '}', ';' -> return text.substring(index + 1, offset)
                }
                index--
            }
            return text.substring(0, offset.coerceIn(0, text.length))
        }

        private fun currentLinePrefix(
            document: Document,
            offset: Int,
        ): String {
            val lineStart = document.getLineStartOffset(document.getLineNumber(offset))
            return document.text.substring(lineStart, offset)
        }

        private fun insideOpenSwitchBody(
            text: String,
            offset: Int,
        ): Boolean {
            val beforeCaret = text.substring(0, offset.coerceIn(0, text.length))
            val switchIndex = beforeCaret.lastIndexOf("switch_")
            if (switchIndex < 0) {
                return false
            }
            val openBrace = beforeCaret.indexOf('{', switchIndex)
            if (openBrace < 0) {
                return false
            }
            val closeBrace = beforeCaret.indexOf('}', openBrace)
            return closeBrace < 0
        }

        private fun currentWordPrefix(
            text: String,
            offset: Int,
        ): String {
            var index = (offset - 1).coerceAtMost(text.lastIndex)
            while (index >= 0 && isIdentifierPart(text[index])) {
                index--
            }
            return text.substring(index + 1, offset)
        }

        private fun isIdentifierPart(char: Char): Boolean = char.isLetterOrDigit() || char == '_' || char == '.' || char == ':'

        private fun previousNonWhitespace(
            text: String,
            offset: Int,
        ): Char? {
            var index = (offset - 1).coerceAtMost(text.lastIndex)
            while (index >= 0) {
                val char = text[index]
                if (!char.isWhitespace()) {
                    return char
                }
                index--
            }
            return null
        }

        private fun firstLineBreakAfter(
            text: String,
            offset: Int,
        ): Int? {
            val newline = text.indexOf('\n', offset).takeIf { it >= 0 }
            val carriageReturn = text.indexOf('\r', offset).takeIf { it >= 0 }
            return listOfNotNull(newline, carriageReturn).minOrNull()
        }

        private fun isStatementPosition(position: PsiElement): Boolean {
            if (position.parentOfType<RsStatementList>() != null) {
                return true
            }
            val statement = position.parentOfType<RsStatement>()
            if (statement != null && PsiTreeUtil.isAncestor(statement, position, false)) {
                return true
            }
            return position.parentOfType<RsBlockStatement>() != null
        }
    }

    private companion object {
        private const val TRIGGER_PRIORITY = 120.0
        private const val TYPE_PRIORITY = 110.0
        private const val DECLARATION_PRIORITY = 105.0
        private const val STATEMENT_KEYWORD_PRIORITY = 90.0
        private const val LOCAL_VARIABLE_PRIORITY = 85.0
        private const val VARIABLE_PRIORITY = 80.0
        private const val CONSTANT_PRIORITY = 78.0
        private const val SCOPED_VARIABLE_PRIORITY = 76.0
        private const val SYMBOL_PRIORITY = 82.0
        private const val SCRIPT_REFERENCE_PRIORITY = 75.0
        private const val COMMAND_PRIORITY = 45.0
        private const val PROC_PRIORITY = 35.0
        private const val EXPRESSION_KEYWORD_PRIORITY = 30.0
        private const val TYPED_CONTEXT_CONSTANT_PRIORITY = -20.0
        private const val CONSTANT_REFERENCE_PRIORITY = 90.0
        private const val SCOPED_VARIABLE_REFERENCE_PRIORITY = 90.0
        private const val OPERATOR_PRIORITY = 95.0
        private const val EXPECTED_TYPE_PRIORITY_BOOST = 40.0
        private const val UNEXPECTED_TYPE_PRIORITY_PENALTY = -80.0
        private const val STRONG_COMPLETION_GROUP = 0
        private const val GENERIC_EXPRESSION_COMPLETION_GROUP = 50
        private const val MACRO_COMPLETION_GROUP = 100
        private const val MAX_SYMBOL_INDEX_COMPLETIONS = 200
        private const val EXACT_PREFIX_PRIORITY_BOOST = 250.0
        private const val PREFIX_PRIORITY_BOOST = 200.0
        private const val SUBSTRING_PREFIX_PRIORITY_BOOST = 160.0
        private const val FUZZY_PREFIX_PRIORITY_BOOST = 100.0
        private val CONDITION_OPERATORS = listOf("=", "!", ">", "<", ">=", "<=")
        private val LOCAL_DECLARATION_REGEX = """\b(def_[A-Za-z0-9_.:]+)\s+(\$[A-Za-z0-9_.:]+)""".toRegex()
        private val LOCAL_DECLARATION_INITIALIZER_REGEX =
            """\bdef_([A-Za-z0-9_.:]+)\s+\$[A-Za-z0-9_.:]+\s*=""".toRegex()
        private val ASSIGNMENT_VALUE_REGEX = """(\$[A-Za-z0-9_.:]+)\s*=\s*$""".toRegex()
        private val CONDITION_VALUE_REGEX = """(\$[A-Za-z0-9_.:]+)\s*(>=|<=|[=!<>])\s*$""".toRegex()
        private val CONDITION_OPERATOR_REGEX = """>=|<=|[=!<>]""".toRegex()
        private val CONDITION_OPERAND_REGEX = """\$[A-Za-z0-9_.:]+|[A-Za-z0-9_.:]+|\d+""".toRegex()
        private val RETURN_VALUE_REGEX = """\breturn\s*\([^)]*$""".toRegex()
        private val PARENTHESIZED_GROUP_REGEX = """\(([^)]*)\)""".toRegex()
        private val SWITCH_SELECTOR_REGEX = """\bswitch_([A-Za-z0-9_.:]+)\s*\([^)]*$""".toRegex()
        private val SWITCH_CASE_TYPE_REGEX = """\bswitch_([A-Za-z0-9_.:]+)\s*\([^)]*\)\s*\{[^}]*$""".toRegex()
    }
}

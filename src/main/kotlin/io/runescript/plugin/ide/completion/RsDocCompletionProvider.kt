package io.runescript.plugin.ide.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.openapi.module.ModuleUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext
import io.runescript.plugin.ide.parameter.RsParameterBehaviorOptionKind
import io.runescript.plugin.ide.parameter.RsParameterBehaviorRegistry
import io.runescript.plugin.ide.parameter.RsParameterBehaviorResolver
import io.runescript.plugin.lang.doc.parser.RsDocKnownTag
import io.runescript.plugin.lang.doc.psi.api.RsDoc
import io.runescript.plugin.lang.psi.RsParameter
import io.runescript.plugin.symbollang.psi.RsSymSymbol
import io.runescript.plugin.symbollang.psi.index.RsSymbolIndex
import io.runescript.plugin.symbollang.psi.resolveToSymTypeName

class RsDocCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet,
    ) {
        val doc = parameters.position.containingDoc() ?: return
        val linePrefix = parameters.linePrefix()

        CONSTANT_OPTIONS.matchEntire(linePrefix)?.let { match ->
            addConstantOptions(parameters.position, match.groupValues[1], result)
            return
        }
        PARAMETER_BEHAVIOR.matchEntire(linePrefix)?.let { match ->
            addParameterBehaviors(doc, match.groupValues[1], match.groupValues[2], result)
            return
        }
        PARAMETER_SUBJECT.matchEntire(linePrefix)?.let { match ->
            addParameterSubjects(doc, match.groupValues[1], match.groupValues[2], result)
            return
        }
        TAG_NAME.matchEntire(linePrefix)?.let { match ->
            addTagNames(match.groupValues[1], result)
        }
    }

    private fun addTagNames(
        prefix: String,
        result: CompletionResultSet,
    ) {
        val prefixed = result.withPrefixMatcher("@$prefix")
        for (tag in RsDocKnownTag.entries) {
            val name = "@${tag.name.lowercase()}"
            var lookup =
                LookupElementBuilder
                    .create(name)
                    .withIcon(AllIcons.Nodes.Tag)
                    .withTypeText("RSDoc")
            if (tag.isReferenceRequired) {
                lookup = lookup.withInsertHandler(::appendSpace)
            }
            prefixed.addElement(lookup)
        }
    }

    private fun addParameterSubjects(
        doc: RsDoc,
        tagName: String,
        prefix: String,
        result: CompletionResultSet,
    ) {
        val existingParameters =
            if (tagName == "param") {
                doc
                    .getAllSections()
                    .flatMap { section -> section.findTagsByName("param") }
                    .mapNotNullTo(hashSetOf()) { tag -> tag.getSubjectName() }
            } else {
                emptySet()
            }
        val prefixed = result.withPrefixMatcher(prefix)
        for (parameter in doc.owner
            ?.parameterList
            ?.parameterList
            .orEmpty()) {
            val name = parameter.localVariableExpression?.name ?: continue
            if (name in existingParameters) continue
            prefixed.addElement(parameterLookup(parameter, name))
        }
    }

    private fun addParameterBehaviors(
        doc: RsDoc,
        parameterName: String,
        prefix: String,
        result: CompletionResultSet,
    ) {
        val parameter =
            doc.owner
                ?.parameterList
                ?.parameterList
                .orEmpty()
                .firstOrNull { candidate -> candidate.localVariableExpression?.name == parameterName }
        val existingBehaviors =
            doc
                .getAllSections()
                .flatMap { section -> section.findTagsByName(RsParameterBehaviorResolver.PARAMETER_METADATA_TAG) }
                .filter { tag -> tag.getSubjectName() == parameterName }
                .mapNotNullTo(hashSetOf()) { tag -> RsParameterBehaviorResolver.parseBehavior(tag.getContent())?.id }
        val prefixed = result.withPrefixMatcher(prefix)
        for (definition in RsParameterBehaviorRegistry.definitions) {
            if (definition.id in existingBehaviors) continue
            if (definition.requiredTypeName != null && parameter?.typeName?.text != definition.requiredTypeName) continue
            var lookup =
                LookupElementBuilder
                    .create(definition.id)
                    .withIcon(AllIcons.Nodes.Property)
                    .withTypeText("parameter behavior")
            if (definition.optionKind == RsParameterBehaviorOptionKind.CONSTANTS) {
                lookup = lookup.withInsertHandler(::insertOptionBrackets)
            }
            prefixed.addElement(lookup)
        }
    }

    private fun addConstantOptions(
        position: PsiElement,
        optionsText: String,
        result: CompletionResultSet,
    ) {
        val currentOption = optionsText.substringAfterLast(',').trim().removePrefix("^")
        val existingOptions =
            optionsText
                .substringBeforeLast(',', "")
                .split(',')
                .mapTo(hashSetOf()) { option -> option.trim().removePrefix("^") }
        val project = position.project
        val module = ModuleUtil.findModuleForPsiElement(position)
        val scope = module?.let(GlobalSearchScope::moduleScope) ?: GlobalSearchScope.projectScope(project)
        val prefixed = result.withPrefixMatcher(currentOption)
        val cache = RsCompletionIndexCache.get(project)
        for (key in cache.matchingSymbolKeys(currentOption)) {
            if (key in existingOptions) continue
            val symbols = StubIndex.getElements(RsSymbolIndex.KEY, key, project, scope, RsSymSymbol::class.java)
            for (symbol in symbols) {
                if (resolveToSymTypeName(symbol.containingFile) != "constant") continue
                val name = symbol.name ?: continue
                prefixed.addElement(
                    LookupElementBuilder
                        .create(symbol, name)
                        .withIcon(AllIcons.Nodes.Constant)
                        .withTypeText(symbol.fieldList.getOrNull(1)?.text ?: "constant")
                        .withTailText(
                            symbol.fieldList
                                .lastOrNull()
                                ?.text
                                ?.let { value -> " = $value" },
                            true,
                        ),
                )
            }
        }
    }

    private fun parameterLookup(
        parameter: RsParameter,
        name: String,
    ): LookupElementBuilder =
        LookupElementBuilder
            .create(parameter, name)
            .withIcon(AllIcons.Nodes.Parameter)
            .withTypeText(parameter.typeName.text)
            .withInsertHandler(::appendSpace)

    private fun CompletionParameters.linePrefix(): String {
        val document = editor.document
        val offset = offset.coerceIn(0, document.textLength)
        val line = document.getLineNumber(offset)
        val start = document.getLineStartOffset(line)
        return document.charsSequence.subSequence(start, offset).toString()
    }

    private fun PsiElement.containingDoc(): RsDoc? = (this as? RsDoc) ?: PsiTreeUtil.getParentOfType(this, RsDoc::class.java, false)

    private fun appendSpace(
        context: InsertionContext,
        item: LookupElement,
    ) {
        val offset = context.tailOffset
        if (offset >= context.document.textLength || !context.document.charsSequence[offset].isWhitespace()) {
            context.document.insertString(offset, " ")
            context.editor.caretModel.moveToOffset(offset + 1)
        }
    }

    private fun insertOptionBrackets(
        context: InsertionContext,
        item: LookupElement,
    ) {
        val offset = context.tailOffset
        if (offset >= context.document.textLength || context.document.charsSequence[offset] != '[') {
            context.document.insertString(offset, "[]")
        }
        context.editor.caretModel.moveToOffset(offset + 1)
    }

    private companion object {
        private const val LINE_START = "\\s*(?:/\\*\\*|\\*)?\\s*"
        val TAG_NAME = Regex("$LINE_START@([A-Za-z]*)")
        val PARAMETER_SUBJECT = Regex("$LINE_START@(param|parammeta)\\s+([A-Za-z0-9_]*)")
        val PARAMETER_BEHAVIOR = Regex("$LINE_START@parammeta\\s+([A-Za-z0-9_]+)\\s+([A-Za-z]*)")
        val CONSTANT_OPTIONS = Regex("$LINE_START@parammeta\\s+[A-Za-z0-9_]+\\s+constant\\[([^]]*)")
    }
}

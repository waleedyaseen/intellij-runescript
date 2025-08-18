package io.runescript.plugin.ide.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.module.ModuleUtil
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.stubs.StubIndexKey
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.util.ProcessingContext
import io.runescript.plugin.ide.completion.insertHandler.RsScriptInsertHandler
import io.runescript.plugin.lang.psi.RsScript

open class RsCompletionProviderScriptBase(
    private val indexKey: StubIndexKey<String, RsScript>,
    private val insertHandler: RsScriptInsertHandler,
) :
    RsCompletionProviderBase() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val project = parameters.position.project
        val module = ModuleUtil.findModuleForPsiElement(parameters.position)
        val scope = if (module != null) {
            GlobalSearchScope.moduleScope(module)
        } else {
            GlobalSearchScope.projectScope(project)
        }
        val cacheManager = CachedValuesManager.getManager(project)
        val allKeys = StubIndex.getInstance().getAllKeys(indexKey, project)
        for (key in allKeys) {
            val elements = StubIndex.getElements(
                indexKey,
                key,
                project,
                scope,
                RsScript::class.java
            )
            for (element in elements) {
                val lookupElement = cacheManager.getCachedValue(element) {
                    CachedValueProvider.Result.create(
                        createLookupElement(element),
                        element,
                        element.containingFile.virtualFile
                    )
                }
                result.addElement(lookupElement)
            }
        }
    }

    private fun createLookupElement(command: RsScript): LookupElement {
        val typeText = command.returnList?.typeNameList?.joinToString(",") { it.text } ?: "unit"
        val presentationText = buildString {
            append(command.name)
            val parameterList = command.parameterList?.parameterList
            if (parameterList != null) {
                append('(')
                for ((index, parameter) in parameterList.withIndex()) {
                    if (index > 0) {
                        append(", ")
                    }
                    append(parameter.typeName?.text ?: "???")
                    append(' ')
                    append(parameter.localVariableExpression?.name ?: "???")
                }
                append(')')
            }
        }
        return LookupElementBuilder.create(command)
            .withTypeText(typeText)
            .withPresentableText(presentationText)
            .withIcon(command.getIcon(0))
            .withInsertHandler(insertHandler)
    }
}

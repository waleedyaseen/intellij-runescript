package io.runescript.plugin.ide.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import io.runescript.plugin.ide.completion.insertHandler.RsVariableInsertHandler
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.scope.collectVariableDeclarations
import io.runescript.plugin.lang.psi.typechecker.typeCheckedType

class RsCompletionProviderVariables : RsCompletionProviderBase() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val position = parameters.position
        val declarations = position.collectVariableDeclarations()
        if (declarations.isEmpty()) return
        for (declaration in declarations) {
            val element = LookupElementBuilder.create(declaration)
                .withTypeText(declaration.typeCheckedType.representation)
                .withIcon(AllIcons.Nodes.Variable)
                .withInsertHandler(RsVariableInsertHandler)
            result.addElement(
                PrioritizedLookupElement.withPriority(element, 1.0)
            )
        }
    }
}
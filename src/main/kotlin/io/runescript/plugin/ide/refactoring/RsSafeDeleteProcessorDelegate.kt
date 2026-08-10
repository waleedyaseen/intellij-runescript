package io.runescript.plugin.ide.refactoring

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.refactoring.safeDelete.NonCodeUsageSearchInfo
import com.intellij.refactoring.safeDelete.SafeDeleteProcessor
import com.intellij.refactoring.safeDelete.SafeDeleteProcessorDelegate
import com.intellij.refactoring.safeDelete.usageInfo.SafeDeleteReferenceSimpleDeleteUsageInfo
import com.intellij.refactoring.safeDelete.usageInfo.SafeDeleteReferenceUsageInfo
import com.intellij.usageView.UsageInfo
import io.runescript.plugin.ide.codeInsight.intention.documentation.RsRemoveObsoleteDocTagsIntention
import io.runescript.plugin.ide.doc.findDoc
import io.runescript.plugin.lang.psi.RsCallExpression
import io.runescript.plugin.lang.psi.RsElementTypes
import io.runescript.plugin.lang.psi.RsLocalVariableDeclarationStatement
import io.runescript.plugin.lang.psi.RsLocalVariableExpression
import io.runescript.plugin.lang.psi.RsParameter
import io.runescript.plugin.lang.psi.RsScript

class RsSafeDeleteProcessorDelegate : SafeDeleteProcessorDelegate {
    override fun handlesElement(element: PsiElement): Boolean =
        element is RsScript || parameterFor(element) != null || localDeclarationFor(element) != null

    override fun findUsages(
        element: PsiElement,
        allElementsToDelete: Array<out PsiElement>,
        usages: MutableList<in UsageInfo>,
    ): NonCodeUsageSearchInfo {
        val searchElement = parameterFor(element)?.localVariableExpression ?: element
        SafeDeleteProcessor.findGenericElementUsages(searchElement, usages, allElementsToDelete)
        parameterFor(element)?.let { parameter -> collectParameterCallUsages(parameter, searchElement, usages) }
        return NonCodeUsageSearchInfo(
            SafeDeleteProcessor.getDefaultInsideDeletedCondition(allElementsToDelete),
            searchElement,
        )
    }

    override fun getElementsToSearch(
        element: PsiElement,
        allElementsToDelete: Collection<PsiElement>,
    ): Collection<PsiElement> = listOf(parameterFor(element)?.localVariableExpression ?: element)

    override fun getAdditionalElementsToDelete(
        element: PsiElement,
        allElementsToDelete: Collection<PsiElement>,
        askUser: Boolean,
    ): Collection<PsiElement> = emptyList()

    override fun preprocessUsages(
        project: Project,
        usages: Array<out UsageInfo>,
    ): Array<out UsageInfo> = usages

    override fun prepareForDeletion(element: PsiElement) {
        when (element) {
            is RsScript -> {
                element.findDoc()?.delete()
            }

            else -> {
                val parameter = parameterFor(element)
                if (parameter != null) {
                    deleteParameter(parameter)
                } else {
                    localDeclarationFor(element)?.let { declaration ->
                        RsLocalVariableDeletion.deletePreservingEffects(element.project, declaration)
                    }
                }
            }
        }
    }

    override fun isToSearchInComments(element: PsiElement): Boolean = false

    override fun setToSearchInComments(
        element: PsiElement,
        enabled: Boolean,
    ) = Unit

    override fun isToSearchForTextOccurrences(element: PsiElement): Boolean = false

    override fun setToSearchForTextOccurrences(
        element: PsiElement,
        enabled: Boolean,
    ) = Unit

    private fun collectParameterCallUsages(
        parameter: RsParameter,
        referencedElement: PsiElement,
        usages: MutableList<in UsageInfo>,
    ) {
        val script = PsiTreeUtil.getParentOfType(parameter, RsScript::class.java) ?: return
        val parameters = script.parameterList?.parameterList.orEmpty()
        val parameterIndex = parameters.indexOf(parameter)
        if (parameterIndex < 0) return

        for (call in findScriptCalls(script)) {
            val arguments = call.argumentList?.expressionList.orEmpty()
            val complete = call.argumentList?.rparen != null
            if (complete && arguments.size == parameters.size) {
                usages += RsSafeDeleteCallArgumentUsageInfo(call, referencedElement, parameterIndex)
            } else {
                usages += SafeDeleteReferenceSimpleDeleteUsageInfo(call, referencedElement, false)
            }
        }
    }

    private fun deleteParameter(parameter: RsParameter) {
        val script = PsiTreeUtil.getParentOfType(parameter, RsScript::class.java) ?: return
        val scriptPointer = SmartPointerManager.createPointer(script)
        deleteCommaSeparatedElement(parameter)

        val manager = PsiDocumentManager.getInstance(script.project)
        manager.getDocument(script.containingFile)?.let(manager::doPostponedOperationsAndUnblockDocument)
        manager.commitAllDocuments()
        val updatedScript = scriptPointer.element ?: return
        val document = manager.getDocument(updatedScript.containingFile) ?: return
        RsRemoveObsoleteDocTagsIntention().applyTo(script.project, document, updatedScript)
    }

    private fun parameterFor(element: PsiElement): RsParameter? =
        when (element) {
            is RsParameter -> element
            is RsLocalVariableExpression -> element.parent as? RsParameter
            else -> null
        }

    private fun localDeclarationFor(element: PsiElement): RsLocalVariableDeclarationStatement? =
        (element as? RsLocalVariableExpression)?.parent as? RsLocalVariableDeclarationStatement
}

private class RsSafeDeleteCallArgumentUsageInfo(
    call: PsiElement,
    parameter: PsiElement,
    private val parameterIndex: Int,
) : SafeDeleteReferenceUsageInfo(call, parameter, true) {
    override fun deleteElement() {
        val call = element as? RsCallExpression ?: return
        val argument = call.argumentList?.expressionList?.getOrNull(parameterIndex) ?: return
        deleteCommaSeparatedElement(argument)
    }
}

private fun deleteCommaSeparatedElement(element: PsiElement) {
    val parent = element.parent
    val previous = PsiTreeUtil.skipWhitespacesBackward(element)
    val next = PsiTreeUtil.skipWhitespacesForward(element)
    when {
        previous?.node?.elementType == RsElementTypes.COMMA -> {
            parent.deleteChildRange(previous, element)
        }

        next?.node?.elementType == RsElementTypes.COMMA -> {
            val trailingWhitespace = next.nextSibling as? PsiWhiteSpace
            parent.deleteChildRange(element, trailingWhitespace ?: next)
        }

        else -> {
            element.delete()
        }
    }
}

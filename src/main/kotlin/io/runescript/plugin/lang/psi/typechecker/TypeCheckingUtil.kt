package io.runescript.plugin.lang.psi.typechecker

import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.psi.util.parentOfType
import io.runescript.plugin.ide.neptune.neptuneModuleData
import io.runescript.plugin.lang.psi.RsExpression
import io.runescript.plugin.lang.psi.typechecker.diagnostics.Diagnostic
import io.runescript.plugin.lang.psi.typechecker.diagnostics.Diagnostics
import io.runescript.plugin.lang.psi.typechecker.symbol.LocalVariableTable
import io.runescript.plugin.lang.psi.typechecker.type.Type

object TypeCheckingUtil {

    fun ensureTypeChecked(expression: RsExpression) {
        typeCheck(expression)
    }

    fun typeCheck(element: PsiElement, rootTable: LocalVariableTable = LocalVariableTable()): List<Diagnostic> {
        val typeCheckerRoot = findTypeCheckerRoot(element) ?: return emptyList()
        val moduleData = typeCheckerRoot.neptuneModuleData ?: return emptyList()
        return CachedValuesManager.getCachedValue(typeCheckerRoot) {
            val triggerManager = moduleData.triggers
            val typeManager = moduleData.types
            val diagnostics = Diagnostics()
            val preTypeChecking = PreTypeChecking(
                triggerManager,
                typeManager,
                diagnostics,
                rootTable,
                moduleData.arraysV2
            )
            typeCheckerRoot.accept(preTypeChecking)

            val typeChecking = TypeChecking(
                triggerManager,
                typeManager,
                diagnostics,
                rootTable,
                moduleData.dynamicCommandHandlers,
                moduleData.symbolLoaders,
                moduleData.arraysV2
            )
            typeCheckerRoot.accept(typeChecking)

            CachedValueProvider.Result.create(diagnostics, PsiModificationTracker.MODIFICATION_COUNT)
        }.diagnostics
    }

    fun getErrors(element: PsiElement): List<Diagnostic> {
        val diagnostics = typeCheck(element)
        return diagnostics.filter {
            it.element == element && it.isError()
        }
    }

    private fun findTypeCheckerRoot(element: PsiElement): RsInferenceDataHolder? {
        return element.parentOfType<RsInferenceDataHolder>(true)
    }
}
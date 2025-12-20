package io.runescript.plugin.lang.psi.typechecker

import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.parentOfType
import io.runescript.plugin.ide.neptune.neptuneModuleData
import io.runescript.plugin.lang.psi.RsExpression
import io.runescript.plugin.lang.psi.typechecker.diagnostics.Diagnostic
import io.runescript.plugin.lang.psi.typechecker.diagnostics.Diagnostics
import io.runescript.plugin.lang.psi.typechecker.symbol.LocalVariableTable

object TypeCheckingUtil {

    fun ensureTypeChecked(expression: RsExpression) {
        typeCheck(expression)
    }

    fun typeCheck(element: PsiElement, rootTable: LocalVariableTable = LocalVariableTable()): List<Diagnostic> {
        val typeCheckerRoot = findTypeCheckerRoot(element) ?: return emptyList()
        synchronized(typeCheckerRoot) {
            return CachedValuesManager.getCachedValue(typeCheckerRoot) {
                val moduleData = typeCheckerRoot.neptuneModuleData
                if (moduleData == null) {
                    CachedValueProvider.Result.create(null)
                } else {
                    typeCheckerRoot.typeCheckerData = TypeCheckerDataHolder()
                    val diagnostics = Diagnostics()
                    val preTypeChecking = PreTypeChecking(
                        moduleData.resolvedData.triggers,
                        moduleData.resolvedData.types,
                        diagnostics,
                        rootTable,
                        moduleData.arraysV2
                    )
                    typeCheckerRoot.accept(preTypeChecking)

                    val typeChecking = TypeChecking(
                        moduleData.resolvedData.triggers,
                        moduleData.resolvedData.types,
                        diagnostics,
                        rootTable,
                        moduleData.resolvedData.dynamicCommandHandlers,
                        moduleData.resolvedData.symbolLoaders,
                        moduleData.arraysV2
                    )
                    typeCheckerRoot.accept(typeChecking)

                    CachedValueProvider.Result.create(diagnostics, typeCheckerRoot)
                }
            }?.diagnostics ?: emptyList()
        }
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
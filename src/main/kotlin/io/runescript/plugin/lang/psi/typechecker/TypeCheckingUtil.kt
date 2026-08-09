package io.runescript.plugin.lang.psi.typechecker

import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.psi.util.parentOfType
import io.runescript.plugin.ide.neptune.neptuneModuleData
import io.runescript.plugin.lang.RuneScript
import io.runescript.plugin.lang.psi.RsExpression
import io.runescript.plugin.lang.psi.typechecker.diagnostics.Diagnostic
import io.runescript.plugin.lang.psi.typechecker.diagnostics.Diagnostics
import io.runescript.plugin.lang.psi.typechecker.symbol.LocalVariableTable
import io.runescript.plugin.symbollang.RuneScriptSymbol

object TypeCheckingUtil {
    private data class ActiveAnalysis(
        val root: RsInferenceDataHolder,
        val data: TypeCheckerDataHolder,
    )

    private data class TypeCheckingResult(
        val diagnostics: List<Diagnostic>,
        val data: TypeCheckerDataHolder,
    )

    private val activeAnalyses = ThreadLocal.withInitial { ArrayDeque<ActiveAnalysis>() }

    fun ensureTypeChecked(expression: RsExpression) {
        typeCheck(expression)
    }

    fun typeCheck(element: PsiElement): List<Diagnostic> {
        val typeCheckerRoot = findTypeCheckerRoot(element) ?: return emptyList()
        if (activeData(typeCheckerRoot) != null) {
            return emptyList()
        }

        val result =
            CachedValuesManager.getCachedValue(typeCheckerRoot) {
                calculate(typeCheckerRoot)
            } ?: return emptyList()

        typeCheckerRoot.typeCheckerData = result.data
        return result.diagnostics
    }

    internal fun dataFor(element: PsiElement): TypeCheckerDataHolder? {
        val root = findTypeCheckerRoot(element) ?: return null
        return activeData(root) ?: root.typeCheckerData
    }

    private fun calculate(root: RsInferenceDataHolder): CachedValueProvider.Result<TypeCheckingResult?> {
        val moduleData =
            root.neptuneModuleData
                ?: return CachedValueProvider.Result.create(null, root)
        val data = TypeCheckerDataHolder()
        val diagnostics = Diagnostics()
        val rootTable = LocalVariableTable()

        withActiveAnalysis(root, data) {
            val preTypeChecking =
                PreTypeChecking(
                    moduleData.resolvedData.triggers,
                    moduleData.resolvedData.types,
                    diagnostics,
                    rootTable,
                    moduleData.arraysV2,
                )
            root.accept(preTypeChecking)

            val typeChecking =
                TypeChecking(
                    moduleData.resolvedData.triggers,
                    moduleData.resolvedData.types,
                    diagnostics,
                    rootTable,
                    moduleData.resolvedData.dynamicCommandHandlers,
                    moduleData.resolvedData.symbolLoaders,
                    moduleData.arraysV2,
                )
            root.accept(typeChecking)
        }

        val result = TypeCheckingResult(diagnostics.diagnostics.toList(), data)
        val languageModifications =
            PsiModificationTracker
                .getInstance(root.project)
                .forLanguages { it == RuneScript || it == RuneScriptSymbol }
        return CachedValueProvider.Result.create(result, root, moduleData, languageModifications)
    }

    private fun activeData(root: RsInferenceDataHolder): TypeCheckerDataHolder? =
        activeAnalyses.get().firstOrNull { it.root === root }?.data

    private inline fun withActiveAnalysis(
        root: RsInferenceDataHolder,
        data: TypeCheckerDataHolder,
        action: () -> Unit,
    ) {
        val analyses = activeAnalyses.get()
        analyses.addFirst(ActiveAnalysis(root, data))
        try {
            action()
        } finally {
            analyses.removeFirst()
            if (analyses.isEmpty()) {
                activeAnalyses.remove()
            }
        }
    }

    private fun findTypeCheckerRoot(element: PsiElement): RsInferenceDataHolder? = element.parentOfType<RsInferenceDataHolder>(true)
}

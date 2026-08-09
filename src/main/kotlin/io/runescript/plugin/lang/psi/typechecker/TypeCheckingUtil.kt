package io.runescript.plugin.lang.psi.typechecker

import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.parentOfType
import io.runescript.plugin.ide.neptune.neptuneModuleData
import io.runescript.plugin.lang.psi.RsExpression
import io.runescript.plugin.lang.psi.localModificationTracker
import io.runescript.plugin.lang.psi.stubIndexModificationTracker
import io.runescript.plugin.lang.psi.typechecker.diagnostics.Diagnostic
import io.runescript.plugin.lang.psi.typechecker.diagnostics.Diagnostics
import io.runescript.plugin.lang.psi.typechecker.symbol.LocalVariableTable

object TypeCheckingUtil {
    private data class ActiveAnalysis(
        val root: RsInferenceDataHolder,
        val data: TypeCheckerDataHolder,
    )

    private data class TypeCheckingResult(
        val diagnostics: List<Diagnostic>,
        val data: TypeCheckerDataHolder,
    )

    private val TYPE_CHECKING_CACHE_KEY =
        Key.create<CachedValue<TypeCheckingResult?>>("io.runescript.plugin.typeCheckingResult")

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
            CachedValuesManager
                .getManager(typeCheckerRoot.project)
                .getCachedValue(
                    typeCheckerRoot,
                    TYPE_CHECKING_CACHE_KEY,
                    { calculate(typeCheckerRoot) },
                    false,
                ) ?: return emptyList()

        typeCheckerRoot.typeCheckerData = result.data
        return result.diagnostics
    }

    internal fun dataFor(element: PsiElement): TypeCheckerDataHolder? {
        activeDataFor(element)?.let { return it }
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
        val rootFile = root.containingFile
        val externalFiles = linkedSetOf<com.intellij.psi.PsiFile>()
        val collectDependency: (PsiElement) -> Unit = { dependency ->
            dependency.containingFile?.takeIf { it !== rootFile }?.let(externalFiles::add)
        }

        withActiveAnalysis(root, data) {
            val preTypeChecking =
                PreTypeChecking(
                    moduleData.resolvedData.triggers,
                    moduleData.resolvedData.types,
                    diagnostics,
                    rootTable,
                    moduleData.arraysV2,
                    collectDependency,
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
                    collectDependency,
                )
            root.accept(typeChecking)
        }

        val result = TypeCheckingResult(diagnostics.diagnostics.toList(), data)
        return CachedValueProvider.Result.create(
            result,
            rootFile.localModificationTracker(),
            moduleData,
            root.project.stubIndexModificationTracker(),
            *externalFiles.map { it.localModificationTracker() }.toTypedArray(),
        )
    }

    private fun activeData(root: RsInferenceDataHolder): TypeCheckerDataHolder? =
        activeAnalyses.get().firstOrNull { it.root === root }?.data

    private fun activeDataFor(element: PsiElement): TypeCheckerDataHolder? {
        val analysis = activeAnalyses.get().firstOrNull() ?: return null
        return analysis.data.takeIf { analysis.root.containingFile === element.containingFile }
    }

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

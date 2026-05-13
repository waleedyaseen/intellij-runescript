package io.runescript.plugin.ide.completion

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.stubs.StubIndexKey
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.stubs.index.RsClientScriptIndex
import io.runescript.plugin.lang.stubs.index.RsCommandScriptIndex
import io.runescript.plugin.lang.stubs.index.RsProcScriptIndex
import io.runescript.plugin.symbollang.psi.index.RsSymbolIndex
import io.runescript.plugin.symbollang.psi.index.RsSymbolTypeIndex

internal class RsCompletionIndexCache private constructor(
    private val commandScripts: NameSet,
    private val procScripts: NameSet,
    private val clientScripts: NameSet,
    private val symbols: NameSet,
    private val typedSymbols: NameSet,
) {
    fun typedSymbolKeys(typePrefix: String): Sequence<String> =
        typedSymbols
            .keys
            .asSequence()
            .filter { it.startsWith(typePrefix) }

    fun matchingScriptKeys(
        indexKey: StubIndexKey<String, RsScript>,
        prefix: String,
    ): Sequence<String> =
        when (indexKey) {
            RsCommandScriptIndex.KEY -> commandScripts
            RsProcScriptIndex.KEY -> procScripts
            RsClientScriptIndex.KEY -> clientScripts
            else -> NameSet.EMPTY
        }.matching(prefix)

    fun matchingSymbolKeys(prefix: String): Sequence<String> = symbols.matching(prefix)

    fun matchingTypedSymbolKeys(
        typePrefix: String,
        prefix: String,
    ): Sequence<String> =
        typedSymbols
            .matching(prefix)
            .filter { it.startsWith(typePrefix) }

    private class NameSet(
        val keys: List<String>,
        private val nameText: (String) -> String = { it },
    ) {
        private val buckets: Map<String, List<String>> =
            buildMap<String, MutableList<String>> {
                for (key in keys) {
                    for (bucket in bucketKeys(nameText(key))) {
                        getOrPut(bucket) { mutableListOf() }.add(key)
                    }
                }
            }

        fun matching(prefix: String): Sequence<String> {
            val bucket = bucketKey(prefix)
            return if (bucket.isBlank()) {
                keys.asSequence()
            } else {
                buckets[bucket]?.asSequence() ?: keys.asSequence()
            }
        }

        companion object {
            val EMPTY = NameSet(emptyList())

            private fun bucketKeys(text: String): Set<String> {
                val normalized = normalize(text)
                return normalized
                    .split('.', '_', ':')
                    .asSequence()
                    .plus(normalized)
                    .map { it.take(2) }
                    .filter { it.length == 2 }
                    .toSet()
            }

            private fun bucketKey(text: String): String =
                normalize(text)
                    .take(2)

            private fun normalize(text: String): String =
                text
                    .substringBefore(COMPLETION_UTIL_MARKER)
                    .trimStart('.', '$', '~', '^', '%')
                    .lowercase()
        }
    }

    companion object {
        private const val COMPLETION_UTIL_MARKER = "IntellijIdeaRulezzz"
        private val CACHE_KEY =
            Key.create<CachedValue<RsCompletionIndexCache>>("io.runescript.plugin.ide.completion.RsCompletionIndexCache")

        fun get(project: Project): RsCompletionIndexCache =
            if (project.basePath == null) {
                createValue(project)
            } else {
                CachedValuesManager
                    .getManager(project)
                    .getCachedValue(project, CACHE_KEY, { create(project) }, false)
            }

        private fun create(project: Project): CachedValueProvider.Result<RsCompletionIndexCache> {
            val stubIndex = StubIndex.getInstance()
            return CachedValueProvider.Result.create(
                createValue(project),
                stubIndex.getStubIndexModificationTracker(project),
                PsiModificationTracker.getInstance(project),
            )
        }

        private fun createValue(project: Project): RsCompletionIndexCache {
            val stubIndex = StubIndex.getInstance()
            return RsCompletionIndexCache(
                commandScripts = NameSet(stubIndex.getAllKeys(RsCommandScriptIndex.KEY, project).toList()),
                procScripts = NameSet(stubIndex.getAllKeys(RsProcScriptIndex.KEY, project).toList()),
                clientScripts = NameSet(stubIndex.getAllKeys(RsClientScriptIndex.KEY, project).toList()),
                symbols = NameSet(stubIndex.getAllKeys(RsSymbolIndex.KEY, project).toList()),
                typedSymbols =
                    NameSet(stubIndex.getAllKeys(RsSymbolTypeIndex.KEY, project).toList()) { key ->
                        key.substringAfter('\t', key)
                    },
            )
        }
    }
}

package io.runescript.plugin.lang.psi

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ModificationTracker
import com.intellij.psi.PsiFile
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.stubs.StubIndexKey

fun Project.stubIndexModificationTracker(): ModificationTracker = StubIndex.getInstance().getStubIndexModificationTracker(this)

fun <K, Psi : com.intellij.psi.PsiElement> Project.stubIndexModificationTracker(indexKey: StubIndexKey<K, Psi>): ModificationTracker {
    val stubIndex = StubIndex.getInstance()
    return runCatching {
        // IntelliJ only exposes per-index tracking on the implementation in current platform builds.
        // Resolve it defensively so older/newer IDEs retain correctness through the global fallback.
        val method =
            stubIndex.javaClass.getMethod(
                "getIndexModificationTracker",
                StubIndexKey::class.java,
                Project::class.java,
            )
        method.invoke(stubIndex, indexKey, this) as ModificationTracker
    }.getOrElse {
        stubIndex.getStubIndexModificationTracker(this)
    }
}

fun PsiFile.localModificationTracker(): ModificationTracker = ModificationTracker { viewProvider.modificationStamp }

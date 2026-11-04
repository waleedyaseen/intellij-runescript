package io.runescript.plugin.lang.psi

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ModificationTracker
import com.intellij.psi.PsiFile
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.stubs.StubIndexKey
import com.intellij.psi.util.PsiModificationTracker

fun Project.stubIndexModificationTracker(): ModificationTracker {
    val stubIndex = StubIndex.getInstance()
    return stubIndex.tracker("getStubIndexModificationTracker", arrayOf(Project::class.java), arrayOf(this))
        ?: PsiModificationTracker.getInstance(this)
}

fun <K, Psi : com.intellij.psi.PsiElement> Project.stubIndexModificationTracker(indexKey: StubIndexKey<K, Psi>): ModificationTracker {
    val stubIndex = StubIndex.getInstance()
    return stubIndex.tracker(
        "getIndexModificationTracker",
        arrayOf(StubIndexKey::class.java, Project::class.java),
        arrayOf(indexKey, this),
    ) ?: PsiModificationTracker.getInstance(this)
}

private fun StubIndex.tracker(
    methodName: String,
    parameterTypes: Array<Class<*>>,
    arguments: Array<Any>,
): ModificationTracker? =
    runCatching {
        javaClass.getMethod(methodName, *parameterTypes).invoke(this, *arguments) as ModificationTracker
    }.getOrNull()

fun PsiFile.localModificationTracker(): ModificationTracker = ModificationTracker { viewProvider.modificationStamp }

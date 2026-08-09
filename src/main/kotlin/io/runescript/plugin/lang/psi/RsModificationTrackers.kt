package io.runescript.plugin.lang.psi

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ModificationTracker
import com.intellij.psi.PsiFile
import com.intellij.psi.stubs.StubIndex

fun Project.stubIndexModificationTracker(): ModificationTracker = StubIndex.getInstance().getStubIndexModificationTracker(this)

fun PsiFile.localModificationTracker(): ModificationTracker = ModificationTracker { viewProvider.modificationStamp }

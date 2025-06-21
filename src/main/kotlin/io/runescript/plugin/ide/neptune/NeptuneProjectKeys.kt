package io.runescript.plugin.ide.neptune

import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement

object NeptuneProjectKeys {
    val NEPTUNE_PROJECT_DATA = Key.create<NeptuneProjectData>("neptune.project.data")
}

val Module.neptuneProjectData: NeptuneProjectData?
    get() = getUserData(NeptuneProjectKeys.NEPTUNE_PROJECT_DATA)

val PsiElement.neptuneProjectData: NeptuneProjectData?
    get() = ModuleUtil.findModuleForFile(containingFile)?.neptuneProjectData
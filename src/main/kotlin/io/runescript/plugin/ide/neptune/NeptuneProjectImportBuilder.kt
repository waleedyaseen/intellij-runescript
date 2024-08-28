package io.runescript.plugin.ide.neptune

import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.externalSystem.model.DataNode
import com.intellij.openapi.externalSystem.model.project.ProjectData
import com.intellij.openapi.externalSystem.service.project.ProjectDataManager
import com.intellij.openapi.externalSystem.service.project.wizard.AbstractExternalProjectImportBuilder
import com.intellij.openapi.project.Project
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.ide.RsIcons
import java.io.File

class NeptuneProjectImportBuilder :
    AbstractExternalProjectImportBuilder<NeptuneImportControl>(
        ProjectDataManager.getInstance(),
        { NeptuneImportControl() },
        Neptune.SYSTEM_ID
    ) {
    override fun getName() = RsBundle.message("neptune.name")

    override fun getIcon() = RsIcons.Neptune

    override fun doPrepare(p0: WizardContext) {
    }

    override fun beforeCommit(p0: DataNode<ProjectData>, p1: Project) {
    }

    override fun getExternalProjectConfigToUse(p0: File) = p0

    override fun applyExtraSettings(p0: WizardContext) {
    }

}
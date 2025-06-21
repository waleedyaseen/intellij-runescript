package io.runescript.plugin.ide.neptune

import com.intellij.openapi.externalSystem.model.DataNode
import com.intellij.openapi.externalSystem.model.Key
import com.intellij.openapi.externalSystem.model.ProjectKeys
import com.intellij.openapi.externalSystem.model.project.ProjectData
import com.intellij.openapi.externalSystem.service.project.IdeModifiableModelsProvider
import com.intellij.openapi.externalSystem.service.project.manage.AbstractProjectDataService
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil
import com.intellij.openapi.project.Project

class NeptuneProjectDataService : AbstractProjectDataService<NeptuneProjectData, Module>() {
    override fun getTargetDataKey(): Key<NeptuneProjectData> = Keys.DATA_KEY

    override fun importData(
        toImport: Collection<DataNode<NeptuneProjectData>>,
        projectData: ProjectData?,
        project: Project,
        modelsProvider: IdeModifiableModelsProvider
    ) {
        if (project.isDisposed) return
        for (dataNode in toImport) {
            val moduleData = ExternalSystemApiUtil.findParent(dataNode, ProjectKeys.MODULE)?.data ?: continue
            val module = modelsProvider.findIdeModule(moduleData) ?: continue
            val data = dataNode.data
            module.putUserData(NeptuneProjectKeys.NEPTUNE_PROJECT_DATA, data)
        }

    }

    object Keys {
        val DATA_KEY = Key.create(NeptuneProjectData::class.java, ProjectKeys.MODULE.processingWeight + 1)
    }
}
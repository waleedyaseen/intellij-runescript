package io.runescript.plugin.ide.neptune

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.service
import com.intellij.openapi.externalSystem.autolink.ExternalSystemProjectLinkListener
import com.intellij.openapi.externalSystem.autolink.ExternalSystemUnlinkedProjectAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class NeptuneUnlinkedProjectAware : ExternalSystemUnlinkedProjectAware {
    override val systemId = Neptune.SYSTEM_ID

    override fun isBuildFile(project: Project, buildFile: VirtualFile): Boolean {
        return buildFile.isNeptuneBuildFile
    }

    override fun isLinkedProject(project: Project, externalProjectPath: String): Boolean {
        return project.service<NeptuneSettings>().linkedProjectsSettings.isNotEmpty()
    }

    override fun subscribe(
        project: Project,
        listener: ExternalSystemProjectLinkListener,
        parentDisposable: Disposable
    ) {
        val settings = project.service<NeptuneSettings>()
        settings.subscribe(object: NeptuneSettingsListener {
            override fun onProjectsLinked(settings: MutableCollection<NeptuneProjectSettings>) {
                settings.forEach { listener.onProjectLinked(it.externalProjectPath) }
            }

            override fun onProjectsUnlinked(linkedProjectPaths: MutableSet<String>) {
                linkedProjectPaths.forEach { listener.onProjectUnlinked(it) }
            }
        }, parentDisposable)
    }

    override suspend fun linkAndLoadProjectAsync(project: Project, externalProjectPath: String) {
        NeptuneOpenProjectProvider.linkToExistingProjectAsync(externalProjectPath, project)
    }
}
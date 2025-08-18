package io.runescript.plugin.ide.neptune

import com.intellij.execution.configurations.SimpleJavaParameters
import com.intellij.openapi.components.service
import com.intellij.openapi.externalSystem.ExternalSystemAutoImportAware
import com.intellij.openapi.externalSystem.ExternalSystemConfigurableAware
import com.intellij.openapi.externalSystem.ExternalSystemManager
import com.intellij.openapi.externalSystem.importing.ProjectResolverPolicy
import com.intellij.openapi.externalSystem.service.project.ExternalSystemProjectResolver
import com.intellij.openapi.externalSystem.task.ExternalSystemTaskManager
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.JavaSdk
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.util.Pair
import com.intellij.util.Function
import java.io.File

class NeptuneManager :
    ExternalSystemManager<NeptuneProjectSettings, NeptuneSettingsListener, NeptuneSettings, NeptuneLocalSettings, NeptuneExecutionSettings>,
    StartupActivity, ExternalSystemAutoImportAware, ExternalSystemConfigurableAware {

    private val autoImport = NeptuneAutoImportAware()

    override fun enhanceRemoteProcessing(parameters: SimpleJavaParameters) {
    }

    override fun getSystemId() = Neptune.SYSTEM_ID

    override fun getSettingsProvider(): Function<Project, NeptuneSettings> {
        return Function { project -> project.service<NeptuneSettings>() }
    }

    override fun getLocalSettingsProvider(): Function<Project, NeptuneLocalSettings> {
        return Function { project -> project.service<NeptuneLocalSettings>() }
    }

    override fun getExecutionSettingsProvider(): Function<Pair<Project, String>, NeptuneExecutionSettings> {
        return Function { pair ->
            val project = pair.first
            val systemSettings = project.service<NeptuneSettings>()
            val javaSdk = ProjectJdkTable.getInstance()
                .findJdk(systemSettings.launcherJre)
                ?: error("Java SDK not found")
            val jvmExecutablePath = JavaSdk.getInstance().getVMExecutablePath(javaSdk)
            NeptuneExecutionSettings(
                jvmExecutablePath,
                systemSettings.neptuneHome,
            )
        }
    }

    override fun getProjectResolverClass(): Class<out ExternalSystemProjectResolver<NeptuneExecutionSettings>> {
        return NeptuneProjectResolver::class.java
    }

    override fun getTaskManagerClass(): Class<out ExternalSystemTaskManager<NeptuneExecutionSettings>> {
        return NeptuneSystemTaskManager::class.java
    }

    override fun getExternalProjectDescriptor(): FileChooserDescriptor {
        return FileChooserDescriptorFactory.createSingleFolderDescriptor()
    }

    override fun runActivity(project: Project) {
    }

    override fun getAffectedExternalProjectPath(changedFileOrDirPath: String, project: Project): String? {
        return autoImport.getAffectedExternalProjectPath(changedFileOrDirPath, project)
    }

    override fun getAffectedExternalProjectFiles(projectPath: String, project: Project): MutableList<File> {
        return autoImport.getAffectedExternalProjectFiles(projectPath, project)
    }

    override fun isApplicable(resolverPolicy: ProjectResolverPolicy?): Boolean {
        return resolverPolicy == null || !resolverPolicy.isPartialDataResolveAllowed
    }

    override fun getConfigurable(project: Project): Configurable {
        return NeptuneSystemConfigurable(project)
    }
}
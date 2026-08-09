package io.runescript.plugin.ide.neptune

import com.google.gson.Gson
import com.intellij.execution.configurations.SimpleJavaParameters
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.components.service
import com.intellij.openapi.externalSystem.ExternalSystemAutoImportAware
import com.intellij.openapi.externalSystem.ExternalSystemConfigurableAware
import com.intellij.openapi.externalSystem.ExternalSystemManager
import com.intellij.openapi.externalSystem.importing.ProjectResolverPolicy
import com.intellij.openapi.externalSystem.model.ExternalSystemException
import com.intellij.openapi.externalSystem.service.project.ExternalSystemProjectResolver
import com.intellij.openapi.externalSystem.task.ExternalSystemTaskManager
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.JavaSdk
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.util.Pair
import com.intellij.util.Function
import kotlinx.coroutines.Deferred
import java.nio.file.Path

class NeptuneManager :
    ExternalSystemManager<NeptuneProjectSettings, NeptuneSettingsListener, NeptuneSettings, NeptuneLocalSettings, NeptuneExecutionSettings>,
    ExternalSystemAutoImportAware,
    ExternalSystemConfigurableAware {
    private val autoImport = NeptuneAutoImportAware()

    override fun enhanceRemoteProcessing(parameters: SimpleJavaParameters) {
        parameters.classPath.add(PathManager.getJarPathForClass(Gson::class.java))
        parameters.classPath.add(PathManager.getJarPathForClass(Deferred::class.java))
    }

    override fun getSystemId() = Neptune.SYSTEM_ID

    override fun getSettingsProvider(): Function<Project, NeptuneSettings> = Function { project -> project.service<NeptuneSettings>() }

    override fun getLocalSettingsProvider(): Function<Project, NeptuneLocalSettings> =
        Function { project -> project.service<NeptuneLocalSettings>() }

    override fun getExecutionSettingsProvider(): Function<Pair<Project, String>, NeptuneExecutionSettings> =
        Function { pair ->
            val project = pair.first
            val systemSettings = project.service<NeptuneSettings>()
            val neptuneHome = resolveNeptuneHome(systemSettings.neptuneHome, pair.second)
            val jvmExecutablePath = resolveNeptuneJvmExecutable(systemSettings.launcherJre)
            NeptuneExecutionSettings(
                jvmExecutablePath,
                neptuneHome,
            )
        }

    override fun getProjectResolverClass(): Class<out ExternalSystemProjectResolver<NeptuneExecutionSettings>> =
        NeptuneProjectResolver::class.java

    override fun getTaskManagerClass(): Class<out ExternalSystemTaskManager<NeptuneExecutionSettings>> =
        NeptuneSystemTaskManager::class.java

    override fun getExternalProjectDescriptor(): FileChooserDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()

    override fun getAffectedExternalProjectPath(
        changedFileOrDirPath: String,
        project: Project,
    ): String? = autoImport.getAffectedExternalProjectPath(changedFileOrDirPath, project)

    override fun getAffectedExternalProjectFilePaths(
        projectPath: String,
        project: Project,
    ): List<Path> = autoImport.getAffectedExternalProjectFilePaths(projectPath, project)

    override fun isApplicable(resolverPolicy: ProjectResolverPolicy?): Boolean =
        resolverPolicy == null || !resolverPolicy.isPartialDataResolveAllowed

    override fun getConfigurable(project: Project): Configurable = NeptuneSystemConfigurable(project)
}

internal fun resolveNeptuneJvmExecutable(configuredJre: String): String {
    if (configuredJre.isBlank()) {
        val executableName = if (System.getProperty("os.name").startsWith("Windows")) "java.exe" else "java"
        val currentJvm = Path.of(System.getProperty("java.home"), "bin", executableName)
        if (currentJvm.toFile().isFile) {
            return currentJvm.toString()
        }
        throw ExternalSystemException("The IDE runtime JVM executable was not found at '$currentJvm'")
    }

    val javaSdk =
        ProjectJdkTable
            .getInstance()
            .findJdk(configuredJre)
            ?: throw ExternalSystemException("Configured Neptune JVM '$configuredJre' was not found")
    return JavaSdk.getInstance().getVMExecutablePath(javaSdk)
}

internal fun resolveNeptuneHome(
    configuredHome: String,
    externalProjectPath: String,
): String {
    if (configuredHome.isNotBlank()) {
        getNeptuneHomeValidationError(configuredHome)?.let { error ->
            throw ExternalSystemException(error)
        }
        return Path
            .of(configuredHome)
            .toAbsolutePath()
            .normalize()
            .toString()
    }

    val projectHome = Path.of(normalizeNeptuneProjectPath(externalProjectPath), "sdk")
    if (getNeptuneHomeValidationError(projectHome.toString()) == null) {
        return projectHome.toString()
    }

    throw ExternalSystemException(
        "Neptune home is not configured and no project-local SDK was found at '$projectHome'",
    )
}

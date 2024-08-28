package io.runescript.plugin.ide.neptune

import com.intellij.openapi.externalSystem.importing.ProjectResolverPolicy
import com.intellij.openapi.externalSystem.model.DataNode
import com.intellij.openapi.externalSystem.model.ProjectKeys
import com.intellij.openapi.externalSystem.model.project.ContentRootData
import com.intellij.openapi.externalSystem.model.project.ExternalSystemSourceType
import com.intellij.openapi.externalSystem.model.project.ModuleData
import com.intellij.openapi.externalSystem.model.project.ProjectData
import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskId
import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskNotificationListener
import com.intellij.openapi.externalSystem.service.project.ExternalSystemProjectResolver
import io.runescript.plugin.ide.projectWizard.RsModuleType
import org.slf4j.LoggerFactory
import java.io.File

class NeptuneProjectResolver : ExternalSystemProjectResolver<NeptuneExecutionSettings> {

    private val log = LoggerFactory.getLogger(NeptuneProjectResolver::class.java)

    override fun resolveProjectInfo(
        id: ExternalSystemTaskId,
        projectPath: String,
        isPreviewMode: Boolean,
        settings: NeptuneExecutionSettings?,
        resolverPolicy: ProjectResolverPolicy?,
        listener: ExternalSystemTaskNotificationListener
    ): DataNode<ProjectData>? {

        if (settings == null) {
            check(false) { "NeptuneExecutionSettings is null" }
            return null
        }

        log.info("JVM Arguments: ${settings.jvmArguments.toTypedArray().contentToString()}")
        log.info("Environment Variables: ${settings.env.toMap().entries.joinToString(", ") { "${it.key}=${it.value}" }}")
        log.info("Execution Name: ${settings.arguments}")

        val projectRoot = File(projectPath)
        check(projectRoot.isDirectory)

        val neptuneData = extractNeptuneProjectMetadata(projectRoot)
        val projectName = neptuneData.name

        log.info("Extracted project name: {}", projectName)

        val projectData = ProjectData(
            Neptune.SYSTEM_ID,
            projectName,
            projectRoot.absolutePath,
            projectRoot.absolutePath,
        )
        val projectNode = DataNode(ProjectKeys.PROJECT, projectData, null)
        val moduleNode = projectNode.createModuleNode(projectName, projectRoot.absolutePath)

        moduleNode.createContentRootNode(
            projectRoot.absolutePath,
            listOf("src"),
            listOf("symbols"),
            listOf("pack")
        )

        return projectNode
    }

    private fun DataNode<*>.createModuleNode(moduleName: String, moduleRootPath: String): DataNode<ModuleData> {
        val module = ModuleData(
            moduleName,
            Neptune.SYSTEM_ID,
            RsModuleType.ID,
            moduleName,
            moduleRootPath,
            moduleRootPath
        )
        val moduleNode = createChild(ProjectKeys.MODULE, module)
        return moduleNode
    }

    private fun DataNode<*>.createContentRootNode(
        rootPath: String,
        srcPaths: List<String>,
        symbolPaths: List<String>,
        excludedPaths: List<String>
    ) {
        val content = ContentRootData(Neptune.SYSTEM_ID, rootPath)
        for (srcPath in srcPaths) {
            val absolutePath = File(rootPath, srcPath).absolutePath
            content.storePath(ExternalSystemSourceType.SOURCE, absolutePath)
        }
        for (symbolPath in symbolPaths) {
            val absolutePath = File(rootPath, symbolPath).absolutePath
            content.storePath(ExternalSystemSourceType.SOURCE, absolutePath)
        }
        for (excludedPath in excludedPaths) {
            val absolutePath = File(rootPath, excludedPath).absolutePath
            content.storePath(ExternalSystemSourceType.EXCLUDED, absolutePath)
        }
        createChild(ProjectKeys.CONTENT_ROOT, content)
    }

    override fun cancelTask(taskId: ExternalSystemTaskId, listener: ExternalSystemTaskNotificationListener): Boolean {
        return false
    }

    private fun extractNeptuneProjectMetadata(projectRoot: File): NeptuneProjectData {
        val neptuneFile = projectRoot.resolve("neptune.toml")
        check(neptuneFile.exists())

        val fileText = neptuneFile.readText()

        val regex = Regex("name\\s*=\\s*['\"](.*)['\"]")
        val match = regex.find(fileText)
        val projectName = match?.groupValues?.get(1) ?: projectRoot.name

        return NeptuneProjectData(projectName)
    }

    private data class NeptuneProjectData(
        val name: String
    )
}
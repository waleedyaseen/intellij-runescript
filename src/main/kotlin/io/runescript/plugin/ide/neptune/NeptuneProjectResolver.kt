package io.runescript.plugin.ide.neptune

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
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
import io.runescript.plugin.ide.execution.createNeptuneJvmCommand
import io.runescript.plugin.ide.projectWizard.RsModuleType
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.Boolean
import kotlin.String
import kotlin.collections.List

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
        check(settings != null) { "Neptune settings must not be null" }

        val projectRoot = File(projectPath)
        check(projectRoot.isDirectory)

        val neptuneData = extractNeptuneProjectMetadata(settings, projectRoot) ?: return null
        val projectName = neptuneData.name

        val projectData = ProjectData(
            Neptune.SYSTEM_ID,
            projectName,
            projectRoot.absolutePath,
            projectRoot.absolutePath,
        )
        val projectNode = DataNode(ProjectKeys.PROJECT, projectData, null)
        val moduleNode = projectNode.createModuleNode(projectName, projectRoot.absolutePath)

        val outputPath = neptuneData.writers?.binary?.outputPath
        var excludedPaths = neptuneData.excludePaths
        if (outputPath != null) {
            excludedPaths = excludedPaths + outputPath
        }
        moduleNode.createContentRootNode(
            projectRoot.absolutePath,
            neptuneData.sourcePaths,
            neptuneData.symbolPaths,
            excludedPaths,
        )
        moduleNode.createChild(NeptuneProjectImportDataService.Keys.DATA_KEY, neptuneData.toPersistentData())

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

    private fun extractNeptuneProjectMetadata(
        settings: NeptuneExecutionSettings,
        projectRoot: File
    ): JsonNeptuneProjectData? {
        val neptuneFile = projectRoot.resolve("neptune.toml")
        check(neptuneFile.exists())

        val commandLine = createNeptuneJvmCommand(
            settings.jvmExecutablePath,
            File(settings.neptuneSdkHome),
            projectRoot.absolutePath
        )
        commandLine.addParameter("--config-path")
        commandLine.addParameter(neptuneFile.absolutePath)

        commandLine.addParameter("--print")

        // Turn off logging.
        commandLine.addParameter("--log-level")
        commandLine.addParameter("off")

        val process = commandLine.createProcess()
        val output = process.inputStream.bufferedReader().readText()
        try {
            return Gson().fromJson(output, JsonNeptuneProjectData::class.java)
        } catch (e: Throwable) {
            log.error("Failed to parse Neptune project data", e)
            return null
        }
    }

    class JsonNeptuneProjectData(
        @SerializedName("name")
        val name: String,
        @SerializedName("sourcePaths")
        val sourcePaths: List<String>,
        @SerializedName("symbolPaths")
        val symbolPaths: List<String>,
        @SerializedName("libraryPaths")
        val libraryPaths: List<String>,
        @SerializedName("excludePaths")
        val excludePaths: List<String>,
        @SerializedName("writers")
        val writers: JsonClientScriptWriterConfig?,
        @SerializedName("features")
        val features: JsonCompilerFeatureSet = JsonCompilerFeatureSet(),
    ) {

        fun toPersistentData(): NeptuneProjectImportData {
            return NeptuneProjectImportData(
                name,
                sourcePaths = sourcePaths,
                symbolPaths = symbolPaths,
                dbFindReturnsCount = features.dbFindReturnsCount,
                ccCreateAssertNewArg = features.ccCreateAssertNewArg,
                prefixPostfixExpressions = features.prefixPostfixExpressions,
                arraysV2 = features.arraysV2,
                simplifiedTypeCodes = features.simplifiedTypeCodes,
            )
        }
    }

    data class JsonClientScriptWriterConfig(val binary: JsonBinaryFileWriterConfig? = null)
    data class JsonBinaryFileWriterConfig(val outputPath: String)
    data class JsonCompilerFeatureSet(
        val dbFindReturnsCount: Boolean = false,
        val ccCreateAssertNewArg: Boolean = false,
        val prefixPostfixExpressions: Boolean = false,
        val arraysV2: Boolean = false,
        val simplifiedTypeCodes: Boolean = false,
    )
}
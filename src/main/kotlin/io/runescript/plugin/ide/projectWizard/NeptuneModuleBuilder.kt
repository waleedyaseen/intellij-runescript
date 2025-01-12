package io.runescript.plugin.ide.projectWizard

import com.intellij.openapi.externalSystem.ExternalSystemModulePropertyManager
import com.intellij.openapi.externalSystem.model.ExternalSystemDataKeys
import com.intellij.openapi.externalSystem.service.project.manage.ExternalProjectsManagerImpl
import com.intellij.openapi.externalSystem.service.project.wizard.AbstractExternalModuleBuilder
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.SdkTypeId
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.util.io.systemIndependentPath
import io.runescript.plugin.ide.neptune.Neptune
import io.runescript.plugin.ide.neptune.NeptuneProjectSettings
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

class NeptuneModuleBuilder :
    AbstractExternalModuleBuilder<NeptuneProjectSettings>(Neptune.SYSTEM_ID, NeptuneProjectSettings()) {

    private var creatingNewProject: Boolean = false
    private var rootProjectPath: Path? = null

    override fun setupModule(module: Module) {
        super.setupModule(module)

        FileDocumentManager.getInstance().saveAllDocuments()

        val modulePropertyManager = ExternalSystemModulePropertyManager.getInstance(module)
        modulePropertyManager.setExternalId(Neptune.SYSTEM_ID)
        val rootProjectPath = rootProjectPath!!
        modulePropertyManager.setRootProjectPath(rootProjectPath.systemIndependentPath)
        modulePropertyManager.setLinkedProjectPath(rootProjectPath.systemIndependentPath)

        val project = module.project

        if (creatingNewProject) {
            ExternalProjectsManagerImpl.setupCreatedProject(project)
            project.putUserData(ExternalSystemDataKeys.NEWLY_CREATED_PROJECT, java.lang.Boolean.TRUE)
        } else {
            project.putUserData(ExternalSystemDataKeys.NEWLY_CREATED_PROJECT, java.lang.Boolean.FALSE)
        }
    }

    override fun setupRootModel(modifiableRootModel: ModifiableRootModel) {
        val contentRootPath = contentEntryPath
        if (contentRootPath.isNullOrBlank()) return

        val contentRootDir = File(contentRootPath)
        FileUtilRt.createDirectory(contentRootDir)

        val modelContentRootDir = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(contentRootDir) ?: return
        val moduleContentEntry = modifiableRootModel.addContentEntry(modelContentRootDir)

        val module = modifiableRootModel.module
        val project = module.project

        rootProjectPath = if (creatingNewProject)
            Paths.get(project.basePath!!)
        else
            modelContentRootDir.toNioPath()


        RsProjectTemplate.generateTemplate(module, moduleContentEntry, modelContentRootDir)
    }

    override fun createProject(name: String, path: String): Project? {
        creatingNewProject = true
        return super.createProject(name, path)
    }

    override fun getModuleType(): ModuleType<*> = RsModuleType.INSTANCE

    override fun isSuitableSdkType(sdkType: SdkTypeId): Boolean {
        return true
    }

    override fun getParentGroup() = "RuneScript"

    override fun getName() = "Neptune"

    override fun getPresentableName() = "Neptune"

    override fun getDescription() = "Empty module for developing content using RuneScript language."
}
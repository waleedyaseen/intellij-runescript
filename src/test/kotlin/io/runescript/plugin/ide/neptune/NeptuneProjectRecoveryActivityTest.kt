package io.runescript.plugin.ide.neptune

import com.intellij.openapi.components.service
import com.intellij.openapi.externalSystem.ExternalSystemModulePropertyManager
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import java.nio.file.Path

class NeptuneProjectRecoveryActivityTest : BasePlatformTestCase() {
    override fun setUp() {
        super.setUp()
        project.service<NeptuneSettings>().linkedProjectsSettings = emptyList()
    }

    fun testRefreshesMissingLinkedSiblingProjectOnce() {
        val projectRoot = Path.of(project.basePath!!)
        val siblingRoot = projectRoot.resolveSibling("cache")
        val settings = project.service<NeptuneSettings>()
        settings.linkProject(NeptuneProjectSettings().apply { externalProjectPath = siblingRoot.toString() })
        settings.linkProject(
            NeptuneProjectSettings().apply {
                externalProjectPath = siblingRoot.resolve("neptune.toml").toString()
            },
        )
        val refreshedPaths = mutableListOf<String>()

        refreshMissingNeptuneProjects(project, refreshedPaths::add)

        assertEquals(listOf(normalizeNeptuneProjectPath(siblingRoot.toString())), refreshedPaths)
    }

    fun testDoesNotRefreshLoadedLinkedProject() {
        val projectRoot = normalizeNeptuneProjectPath(project.basePath!!)
        val settings = project.service<NeptuneSettings>()
        settings.linkProject(NeptuneProjectSettings().apply { externalProjectPath = projectRoot })
        ExternalSystemModulePropertyManager.getInstance(module).apply {
            setExternalId(Neptune.SYSTEM_ID)
            setRootProjectPath(projectRoot)
            setLinkedProjectPath(projectRoot)
        }
        val refreshedPaths = mutableListOf<String>()

        refreshMissingNeptuneProjects(project, refreshedPaths::add)

        assertTrue(refreshedPaths.isEmpty())
    }
}

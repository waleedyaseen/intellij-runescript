package io.runescript.plugin.ide.neptune

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.components.service
import com.intellij.openapi.externalSystem.ExternalSystemModulePropertyManager
import com.intellij.testFramework.TestActionEvent
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import java.nio.file.Path

class NeptuneReloadProjectActionTest : BasePlatformTestCase() {
    override fun setUp() {
        super.setUp()
        project.service<NeptuneSettings>().linkedProjectsSettings = emptyList()
    }

    fun testReloadsEveryDistinctLinkedProject() {
        val projectRoot = Path.of(project.basePath!!)
        val cacheRoot = projectRoot.resolveSibling("cache")
        val otherRoot = projectRoot.resolveSibling("other-cache")
        val settings = project.service<NeptuneSettings>()
        settings.linkProject(NeptuneProjectSettings().apply { externalProjectPath = cacheRoot.toString() })
        settings.linkProject(
            NeptuneProjectSettings().apply {
                externalProjectPath = cacheRoot.resolve("neptune.toml").toString()
            },
        )
        settings.linkProject(NeptuneProjectSettings().apply { externalProjectPath = otherRoot.toString() })
        val refreshedPaths = mutableListOf<String>()

        reloadLinkedNeptuneProjects(project, refreshedPaths::add)

        assertEquals(
            setOf(
                normalizeNeptuneProjectPath(cacheRoot.toString()),
                normalizeNeptuneProjectPath(otherRoot.toString()),
            ),
            refreshedPaths.toSet(),
        )
        assertEquals(2, refreshedPaths.size)
    }

    fun testReloadsAlreadyLoadedLinkedProject() {
        val projectRoot = normalizeNeptuneProjectPath(project.basePath!!)
        project.service<NeptuneSettings>().linkProject(
            NeptuneProjectSettings().apply { externalProjectPath = projectRoot },
        )
        ExternalSystemModulePropertyManager.getInstance(module).apply {
            setExternalId(Neptune.SYSTEM_ID)
            setRootProjectPath(projectRoot)
            setLinkedProjectPath(projectRoot)
        }
        val refreshedPaths = mutableListOf<String>()

        reloadLinkedNeptuneProjects(project, refreshedPaths::add)

        assertEquals(listOf(projectRoot), refreshedPaths)
    }

    fun testReloadIsAvailableOnlyWithLinkedProjects() {
        val action = NeptuneReloadProjectAction()
        val unavailableEvent = TestActionEvent.createTestEvent(action, projectDataContext())

        action.update(unavailableEvent)

        assertFalse(unavailableEvent.presentation.isEnabledAndVisible)

        project.service<NeptuneSettings>().linkProject(
            NeptuneProjectSettings().apply { externalProjectPath = project.basePath!! },
        )
        val availableEvent = TestActionEvent.createTestEvent(action, projectDataContext())

        action.update(availableEvent)

        assertTrue(availableEvent.presentation.isEnabledAndVisible)
    }

    fun testReloadActionIsRegistered() {
        val action = ActionManager.getInstance().getAction("RuneScript.ReloadNeptuneProject")

        assertTrue(action is NeptuneReloadProjectAction)
        assertEquals("Reload Neptune Project", action.templatePresentation.text)
    }

    private fun projectDataContext(): DataContext =
        DataContext { dataId ->
            if (CommonDataKeys.PROJECT.`is`(dataId)) project else null
        }
}

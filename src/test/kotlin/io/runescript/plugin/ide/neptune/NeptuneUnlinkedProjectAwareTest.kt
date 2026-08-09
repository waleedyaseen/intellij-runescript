package io.runescript.plugin.ide.neptune

import com.intellij.openapi.components.service
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import kotlinx.coroutines.runBlocking
import java.nio.file.Path

class NeptuneUnlinkedProjectAwareTest : BasePlatformTestCase() {
    override fun setUp() {
        super.setUp()
        project.service<NeptuneSettings>().linkedProjectsSettings = emptyList()
    }

    fun testLinkDetectionIsSpecificToRequestedProject() {
        val linkedRoot = Path.of(project.basePath!!, "linked").toString()
        val otherRoot = Path.of(project.basePath!!, "other").toString()
        val settings = project.service<NeptuneSettings>()
        settings.linkProject(NeptuneProjectSettings().apply { externalProjectPath = linkedRoot })
        val aware = NeptuneUnlinkedProjectAware()

        assertTrue(aware.isLinkedProject(project, linkedRoot))
        assertTrue(aware.isLinkedProject(project, Path.of(linkedRoot, "neptune.toml").toString()))
        assertFalse(aware.isLinkedProject(project, otherRoot))
    }

    fun testUnlinkRemovesOnlyMatchingProject() {
        val linkedRoot = Path.of(project.basePath!!, "linked").toString()
        val otherRoot = Path.of(project.basePath!!, "other").toString()
        val settings = project.service<NeptuneSettings>()
        settings.linkProject(NeptuneProjectSettings().apply { externalProjectPath = linkedRoot })
        settings.linkProject(NeptuneProjectSettings().apply { externalProjectPath = otherRoot })
        val aware = NeptuneUnlinkedProjectAware()

        runBlocking {
            aware.unlinkProject(project, Path.of(linkedRoot, "neptune.toml").toString())
        }

        assertFalse(aware.isLinkedProject(project, linkedRoot))
        assertTrue(aware.isLinkedProject(project, otherRoot))
    }
}

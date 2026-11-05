package io.runescript.plugin.ide.neptune

import junit.framework.TestCase
import java.nio.file.Path

class NeptuneSettingsStateTest : TestCase() {
    fun testLinkedProjectSettingsAreReplaced() {
        val state = NeptuneSettingsState()
        val first = NeptuneProjectSettings().apply { externalProjectPath = "first" }
        val second = NeptuneProjectSettings().apply { externalProjectPath = "second" }

        state.linkedExternalProjectsSettings = mutableSetOf(first)
        state.linkedExternalProjectsSettings = mutableSetOf(second)

        assertEquals(setOf(second), state.linkedExternalProjectsSettings)
    }

    fun testNullLinkedProjectSettingsClearExistingValues() {
        val state = NeptuneSettingsState()
        val project = NeptuneProjectSettings().apply { externalProjectPath = "project" }

        state.linkedExternalProjectsSettings = mutableSetOf(project)
        state.setLinkedExternalProjectsSettings(null)

        assertTrue(state.linkedExternalProjectsSettings.isEmpty())
    }

    fun testEquivalentRootAndConfigSettingsAreNormalizedAndMerged() {
        val state = NeptuneSettingsState()
        val projectRoot = Path.of(System.getProperty("java.io.tmpdir"), "neptune-settings-state-test")
        val rootSettings =
            NeptuneProjectSettings().apply {
                externalProjectPath = projectRoot.toString()
                setModules(setOf(projectRoot.toString()))
            }
        val configSettings =
            NeptuneProjectSettings().apply {
                externalProjectPath = projectRoot.resolve("neptune.toml").toString()
            }

        state.linkedExternalProjectsSettings = mutableSetOf(rootSettings, configSettings)

        val linkedSettings = state.linkedExternalProjectsSettings.single()
        assertEquals(normalizeNeptuneProjectPath(projectRoot.toString()), linkedSettings.externalProjectPath)
        assertEquals(setOf(projectRoot.toString()), linkedSettings.modules)
    }
}

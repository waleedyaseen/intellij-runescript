package io.runescript.plugin.ide.neptune

import junit.framework.TestCase

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
}

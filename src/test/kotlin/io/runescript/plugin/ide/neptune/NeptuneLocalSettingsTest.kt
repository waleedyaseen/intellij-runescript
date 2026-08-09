package io.runescript.plugin.ide.neptune

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class NeptuneLocalSettingsTest : BasePlatformTestCase() {
    fun testLoadStateRestoresPersistedState() {
        val settings = NeptuneLocalSettings(project)
        val state = NeptuneLocalSettingsState()

        settings.loadState(state)

        assertSame(state, settings.state)
    }
}

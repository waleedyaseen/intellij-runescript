package io.runescript.plugin.ide.neptune

import com.intellij.openapi.components.service
import com.intellij.openapi.externalSystem.model.ExternalSystemException
import com.intellij.openapi.util.Pair
import com.intellij.openapi.util.io.FileUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import java.nio.file.Files
import java.nio.file.Path

class NeptuneManagerTest : BasePlatformTestCase() {
    fun testReportsMissingNeptuneHome() {
        val settings = project.service<NeptuneSettings>()
        settings.neptuneHome = ""

        val error = getExecutionSettingsError()

        assertEquals("Neptune home is not configured.", error.message)
    }

    fun testReportsMissingNeptuneJvm() {
        val settings = project.service<NeptuneSettings>()
        settings.neptuneHome = createNeptuneHome().toString()
        settings.launcherJre = ""

        val error = getExecutionSettingsError()

        assertEquals("Neptune JVM is not configured", error.message)
    }

    fun testReportsDeletedNeptuneJvm() {
        val settings = project.service<NeptuneSettings>()
        settings.neptuneHome = createNeptuneHome().toString()
        settings.launcherJre = "missing-jdk"

        val error = getExecutionSettingsError()

        assertEquals("Configured Neptune JVM 'missing-jdk' was not found", error.message)
    }

    private fun getExecutionSettingsError(): ExternalSystemException {
        try {
            NeptuneManager().executionSettingsProvider.`fun`(Pair.create(project, project.basePath!!))
            throw AssertionError("Expected invalid Neptune settings to fail")
        } catch (error: ExternalSystemException) {
            return error
        }
    }

    private fun createNeptuneHome(): Path {
        val home = FileUtil.createTempDirectory("neptune-manager-test-", null, true).toPath()
        val lib = Files.createDirectories(home.resolve("lib"))
        Files.createFile(lib.resolve("neptune-clientscript-compiler-test.jar"))
        return home
    }
}

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

        assertTrue(error.message!!.startsWith("Neptune home is not configured and no project-local SDK was found"))
    }

    fun testUsesProjectLocalNeptuneHome() {
        val projectRoot = FileUtil.createTempDirectory("neptune-project-test-", null, true).toPath()
        val projectHome = createNeptuneHome(projectRoot.resolve("sdk"))

        val resolvedHome = resolveNeptuneHome("", projectRoot.toString())

        assertEquals(projectHome.toString(), resolvedHome)
    }

    fun testUsesIdeRuntimeJvmWhenNeptuneJvmIsNotConfigured() {
        val settings = project.service<NeptuneSettings>()
        settings.neptuneHome = createNeptuneHome().toString()
        settings.launcherJre = ""

        val executionSettings =
            NeptuneManager()
                .executionSettingsProvider
                .`fun`(Pair.create(project, project.basePath!!))

        assertEquals(resolveNeptuneJvmExecutable(""), executionSettings.jvmExecutablePath)
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

    private fun createNeptuneHome(home: Path = FileUtil.createTempDirectory("neptune-manager-test-", null, true).toPath()): Path {
        val lib = Files.createDirectories(home.resolve("lib"))
        Files.createFile(lib.resolve("neptune-clientscript-compiler-test.jar"))
        return home
    }
}

package io.runescript.plugin.ide.neptune

import com.intellij.openapi.components.service
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import java.nio.file.Path

class NeptuneAutoImportAwareTest : BasePlatformTestCase() {
    override fun setUp() {
        super.setUp()
        project.service<NeptuneSettings>().linkedProjectsSettings = emptyList()
    }

    fun testBuildFileChangeRefreshesMatchingLinkedProject() {
        val root = Path.of(project.basePath!!, "linked")
        val settings = project.service<NeptuneSettings>()
        settings.linkProject(NeptuneProjectSettings().apply { externalProjectPath = root.toString() })

        val affected =
            NeptuneAutoImportAware().getAffectedExternalProjectPath(
                root.resolve("neptune.toml").toString(),
                project,
            )

        assertEquals(root.toString(), affected)
    }

    fun testBuildFileChangeIgnoresUnlinkedProject() {
        val root = Path.of(project.basePath!!, "unlinked")

        val affected =
            NeptuneAutoImportAware().getAffectedExternalProjectPath(
                root.resolve("neptune.toml").toString(),
                project,
            )

        assertNull(affected)
    }

    fun testTracksOnlyRootBuildFile() {
        val root = Path.of(project.basePath!!, "linked")
        val paths = NeptuneAutoImportAware().getAffectedExternalProjectFilePaths(root.toString(), project)

        assertEquals(listOf(root.resolve("neptune.toml")), paths)
    }
}

package io.runescript.plugin.ide.neptune

import com.intellij.execution.process.ProcessOutput
import com.intellij.openapi.externalSystem.model.ExternalSystemException
import junit.framework.TestCase
import java.io.File

class NeptuneProjectResolverTest : TestCase() {
    fun testDoesNotAddOutputNestedUnderExcludedDirectory() {
        val excluded =
            NeptuneProjectResolver().collectExcludedPaths(
                File("project"),
                listOf("pack"),
                "pack/server/cs2",
            )

        assertEquals(listOf("pack"), excluded)
    }

    fun testAddsOutputOutsideExcludedDirectories() {
        val excluded =
            NeptuneProjectResolver().collectExcludedPaths(
                File("project"),
                listOf("cache"),
                "pack/server/cs2",
            )

        assertEquals(listOf("cache", "pack/server/cs2"), excluded)
    }

    fun testParsesProjectMetadata() {
        val metadata =
            NeptuneProjectResolver().parseNeptuneProjectMetadata(
                """
                {
                  "name": "example",
                  "sourcePaths": ["src"],
                  "symbolPaths": ["symbols"],
                  "libraryPaths": [],
                  "excludePaths": ["build"],
                  "features": {"longSupport": true}
                }
                """.trimIndent(),
            )

        assertEquals("example", metadata.name)
        assertEquals(listOf("src"), metadata.sourcePaths)
        assertEquals(listOf("symbols"), metadata.symbolPaths)
        assertEquals(listOf("build"), metadata.excludePaths)
        assertTrue(metadata.features.longSupport)
        assertTrue(metadata.toPersistentData().longSupport)
    }

    fun testRejectsInvalidProjectMetadata() {
        try {
            NeptuneProjectResolver().parseNeptuneProjectMetadata("not json")
            fail("Expected invalid metadata to fail")
        } catch (error: ExternalSystemException) {
            assertEquals("Neptune returned invalid project metadata", error.message)
        }
    }

    fun testReportsNeptuneProcessFailures() {
        try {
            NeptuneProjectResolver().parseNeptuneProjectMetadata(
                ProcessOutput("", "invalid config", 7, false, false),
            )
            fail("Expected a failed Neptune process to fail the import")
        } catch (error: ExternalSystemException) {
            assertEquals("Neptune project import failed with exit code 7: invalid config", error.message)
        }
    }
}

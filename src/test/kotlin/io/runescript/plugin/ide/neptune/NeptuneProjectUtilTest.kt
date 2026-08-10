package io.runescript.plugin.ide.neptune

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.runescript.plugin.lang.psi.isSourceFile

class NeptuneProjectUtilTest : BasePlatformTestCase() {
    fun testFindsNestedNeptuneRootForFile() {
        val config = myFixture.addFileToProject("cache/neptune.toml", "")
        val script = myFixture.addFileToProject("cache/scripts/main.cs2", "[proc,main]\n{\n}")

        val root = module.findNeptuneProjectRoot(script.virtualFile)

        assertEquals(config.virtualFile.parent, root)
        assertTrue(script.isSourceFile())
    }

    fun testDoesNotTreatSiblingAsPartOfNestedNeptuneProject() {
        myFixture.addFileToProject("cache/neptune.toml", "")
        val sibling = myFixture.addFileToProject("other/main.cs2", "[proc,main]\n{\n}")

        assertNull(module.findNeptuneProjectRoot(sibling.virtualFile))
        assertFalse(sibling.isSourceFile())
    }

    fun testDirectContentRootProjectStillWorks() {
        val config = myFixture.addFileToProject("neptune.toml", "")
        val script = myFixture.addFileToProject("scripts/main.cs2", "[proc,main]\n{\n}")

        assertEquals(config.virtualFile.parent, module.findNeptuneProjectRoot())
        assertEquals(config.virtualFile.parent, module.findNeptuneProjectRoot(script.virtualFile))
        assertTrue(script.isSourceFile())
    }
}

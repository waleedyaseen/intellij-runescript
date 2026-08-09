package io.runescript.plugin.ide.refactoring

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class RsNamesValidatorTest : BasePlatformTestCase() {
    private val validator = RsNamesValidator()

    fun testAcceptsRuneScriptIdentifiers() {
        assertTrue(validator.isIdentifier("foo", project))
        assertTrue(validator.isIdentifier("foo.bar:baz_1", project))
        assertTrue(validator.isIdentifier("123foo", project))
    }

    fun testRejectsTokensThatDoNotLexAsIdentifiers() {
        assertFalse(validator.isIdentifier("", project))
        assertFalse(validator.isIdentifier("foo+bar", project))
        assertFalse(validator.isIdentifier("123", project))
        assertFalse(validator.isIdentifier("0x10", project))
        assertFalse(validator.isIdentifier("1_2_3_4_5", project))
    }

    fun testRejectsStaticAndTypeKeywords() {
        listOf("if", "return", "int", "def_int", "switch_int").forEach { keyword ->
            assertTrue(validator.isKeyword(keyword, project))
            assertFalse(validator.isIdentifier(keyword, project))
        }
    }
}

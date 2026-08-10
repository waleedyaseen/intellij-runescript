package io.runescript.plugin.ide.inspections

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class RuneScriptUnreachableCodeInspectionTest : BasePlatformTestCase() {
    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(RuneScriptUnreachableCodeInspection())
    }

    fun testRemovesUnreachableStatementAfterReturn() {
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main]
            {
                return;
                foo(1);
            }
            """.trimIndent(),
        )

        val fix = myFixture.getAllQuickFixes().single { action -> action.text == "Remove unreachable statement" }
        myFixture.launchAction(fix)

        assertFalse(myFixture.file.text.contains("foo(1);"))
        assertTrue(myFixture.file.text.contains("return;"))
    }

    fun testDoesNotOfferFixForReachableStatement() {
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main]
            {
                foo(1);
                return;
            }
            """.trimIndent(),
        )

        assertEmpty(myFixture.getAllQuickFixes().filter { action -> action.text == "Remove unreachable statement" })
    }

    fun testRemovesOnlySelectedUnreachableStatement() {
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main]
            {
                return;
                foo(1);
                bar(2);
            }
            """.trimIndent(),
        )

        val fixes = myFixture.getAllQuickFixes().filter { action -> action.text == "Remove unreachable statement" }
        assertEquals(2, fixes.size)
        myFixture.launchAction(fixes.first())

        val remainingCalls = listOf("foo(1);", "bar(2);").count(myFixture.file.text::contains)
        assertEquals(1, remainingCalls)
    }
}

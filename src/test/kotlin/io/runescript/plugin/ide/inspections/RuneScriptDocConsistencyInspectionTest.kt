package io.runescript.plugin.ide.inspections

import io.runescript.plugin.lang.parser.RsParserTestCase

class RuneScriptDocConsistencyInspectionTest : RsParserTestCase() {
    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(RuneScriptDocConsistencyInspection())
    }

    fun testHighlightsAndAddsMissingTags() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                /**
                 * @param first First.
                 */
                [proc,<warning descr="RSDoc is missing signature tags">main</warning>](int ${'$'}first, int ${'$'}second)
                {
                }
                """.trimIndent(),
            )
        myFixture.checkHighlighting()

        val fix = myFixture.getAllQuickFixes().single { it.text == "Add missing RSDoc tags" }
        myFixture.launchAction(fix)

        assertTrue(file.text.contains("@param second"))
    }

    fun testHighlightsObsoleteTags() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                /**
                 * @param old Obsolete.
                 */
                [proc,<warning descr="RSDoc contains obsolete signature tags">main</warning>]
                {
                }
                """.trimIndent(),
            )

        myFixture.checkHighlighting()
        myFixture.launchAction(myFixture.getAllQuickFixes().single { it.text == "Remove obsolete RSDoc tags" })

        assertFalse(file.text.contains("@param old"))
    }

    fun testHighlightsMisorderedTags() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                /**
                 * @param second Second.
                 * @param first First.
                 */
                [proc,<warning descr="RSDoc tags do not match signature order">main</warning>](int ${'$'}first, int ${'$'}second)
                {
                }
                """.trimIndent(),
            )

        myFixture.checkHighlighting()
        myFixture.launchAction(myFixture.getAllQuickFixes().single { it.text == "Reorder RSDoc tags" })

        assertTrue(file.text.indexOf("@param first") < file.text.indexOf("@param second"))
    }
}

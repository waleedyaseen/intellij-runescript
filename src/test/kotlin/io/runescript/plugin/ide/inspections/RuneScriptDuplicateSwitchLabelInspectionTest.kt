package io.runescript.plugin.ide.inspections

import io.runescript.plugin.lang.parser.RsParserTestCase

class RuneScriptDuplicateSwitchLabelInspectionTest : RsParserTestCase() {
    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(RuneScriptDuplicateSwitchLabelInspection())
    }

    fun testHighlightsDuplicateLiteralAndMergesIdenticalCases() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    switch_int (${'$'}value) {
                        case 1, 2 :
                            handle();
                        case <warning descr="Duplicate switch label '1'">1</warning>, 3 :
                            handle();
                    }
                }
                """.trimIndent(),
            )
        myFixture.checkHighlighting()
        val fixes = myFixture.getAllQuickFixes()
        assertContainsElements(fixes.map { it.text }, "Remove duplicate label '1'", "Merge cases containing '1'")

        myFixture.launchAction(fixes.single { it.text == "Merge cases containing '1'" })

        assertTrue(file.text.contains("case 1, 2, 3 :"))
        assertEquals(1, Regex(Regex.escape("handle();")).findAll(file.text).count())
    }

    fun testDifferentBodiesOnlyOfferRemoval() {
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main]
            {
                switch_int (${'$'}value) {
                    case 1 :
                        first();
                    case <warning descr="Duplicate switch label '1'">1</warning> :
                        second();
                }
            }
            """.trimIndent(),
        )
        myFixture.checkHighlighting()

        val fixes = myFixture.getAllQuickFixes().map { it.text }
        assertContainsElements(fixes, "Remove duplicate label '1'")
        assertFalse(fixes.any { it.startsWith("Merge cases") })
    }

    fun testNormalizesIntegerLabelsAndPreservesInterCaseCommentWhenMerging() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    switch_int (${'$'}value) {
                        case 1 : handle();
                        // Hex spelling of the same value.
                        case <warning descr="Duplicate switch label '0x1'">0x1</warning>, 2 : handle();
                    }
                }
                """.trimIndent(),
            )
        myFixture.checkHighlighting()

        myFixture.launchAction(myFixture.getAllQuickFixes().single { it.text == "Merge cases containing '0x1'" })

        assertTrue(file.text.contains("// Hex spelling of the same value."))
        assertTrue(file.text.contains("case 1, 2 :"))
    }

    fun testDuplicateWithinSameCaseDoesNotOfferMerge() {
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main]
            {
                switch_int (${'$'}value) {
                    case 1, <warning descr="Duplicate switch label '0x1'">0x1</warning> : handle();
                }
            }
            """.trimIndent(),
        )
        myFixture.checkHighlighting()

        val fixes = myFixture.getAllQuickFixes().map { it.text }
        assertContainsElements(fixes, "Remove duplicate label '0x1'")
        assertFalse(fixes.any { it.startsWith("Merge cases") })
    }

    fun testNormalizesOverflowingIntegerSpellingsLikeTheTypeChecker() {
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main]
            {
                switch_int (${'$'}value) {
                    case 0x80000000 : first();
                    case <warning descr="Duplicate switch label '-0x80000000'">-0x80000000</warning> : second();
                }
            }
            """.trimIndent(),
        )

        myFixture.checkHighlighting()
    }
}

package io.runescript.plugin.ide.inspections

import com.intellij.openapi.editor.colors.CodeInsightColors
import io.runescript.plugin.lang.parser.RsParserTestCase

class RuneScriptRedundantElseInspectionTest : RsParserTestCase() {
    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(RuneScriptRedundantElseInspection())
    }

    fun testReportsElseAfterReturningBlock() {
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main](boolean ${'$'}ready)
            {
                if (${'$'}ready = true) {
                    return;
                } <weak_warning descr="Redundant 'else' after terminating branch">else</weak_warning> {
                    work();
                }
            }
            """.trimIndent(),
        )

        myFixture.checkHighlighting()
        val redundantElseHighlight =
            myFixture.doHighlighting().single { it.description == "Redundant 'else' after terminating branch" }
        assertEquals(CodeInsightColors.NOT_USED_ELEMENT_ATTRIBUTES, redundantElseHighlight.type.attributesKey)
    }

    fun testRemovesElseAndLiftsBlockBody() {
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main](boolean ${'$'}ready)
            {
                if (${'$'}ready = true) {
                    return;
                } else {
                    work();
                    finish();
                }
            }
            """.trimIndent(),
        )

        launchFix()

        myFixture.checkResult(
            """
            [proc,main](boolean ${'$'}ready)
            {
                if (${'$'}ready = true) {
                    return;
                }
                work();
                finish();
            }
            """.trimIndent(),
        )
    }

    fun testLiftsSingleStatementElseIf() {
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main](boolean ${'$'}ready, boolean ${'$'}fallback)
            {
                if (${'$'}ready = true) return;
                else if (${'$'}fallback = true) work();
            }
            """.trimIndent(),
        )

        launchFix()

        myFixture.checkResult(
            """
            [proc,main](boolean ${'$'}ready, boolean ${'$'}fallback)
            {
                if (${'$'}ready = true) return;
                if (${'$'}fallback = true) work();
            }
            """.trimIndent(),
        )
    }

    fun testPreservesCommentsAroundElse() {
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main](boolean ${'$'}ready)
            {
                if (${'$'}ready = true) {
                    return;
                } /* keep before */ else {
                    // keep inside
                    work();
                }
            }
            """.trimIndent(),
        )

        launchFix()

        myFixture.checkResult(
            """
            [proc,main](boolean ${'$'}ready)
            {
                if (${'$'}ready = true) {
                    return;
                } /* keep before */
                // keep inside
                work();
            }
            """.trimIndent(),
        )
    }

    fun testRecognizesNestedTerminatingConditional() {
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main](boolean ${'$'}first, boolean ${'$'}second)
            {
                if (${'$'}first = true) {
                    if (${'$'}second = true) return;
                    else return;
                } else {
                    work();
                }
            }
            """.trimIndent(),
        )

        assertEquals(2, redundantElseFixes().size)
    }

    fun testRecognizesExhaustiveTerminatingSwitch() {
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main](boolean ${'$'}ready, int ${'$'}value)
            {
                if (${'$'}ready = true) {
                    switch_int (${'$'}value) {
                        case 1 : return;
                        case default : return;
                    }
                } else {
                    work();
                }
            }
            """.trimIndent(),
        )

        assertEquals(1, redundantElseFixes().size)
    }

    fun testDoesNotReportBranchesThatCanContinue() {
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main](boolean ${'$'}first, boolean ${'$'}second, int ${'$'}value)
            {
                if (${'$'}first = true) work(); else finish();
                if (${'$'}first = true) {
                    if (${'$'}second = true) return;
                } else finish();
                if (${'$'}first = true) {
                    switch_int (${'$'}value) {
                        case 1 : return;
                    }
                } else finish();
                if (${'$'}first = true) {
                    while (${'$'}second = true) return;
                } else finish();
            }
            """.trimIndent(),
        )

        assertEmpty(redundantElseFixes())
    }

    fun testDoesNotLiftElseOutOfUnbracedParentBody() {
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main](boolean ${'$'}first, boolean ${'$'}second)
            {
                if (${'$'}first = true)
                    if (${'$'}second = true) return; else work();
                while (${'$'}first = true)
                    if (${'$'}second = true) return; else work();
                if (${'$'}first = true) work();
                else if (${'$'}second = true) return; else finish();
            }
            """.trimIndent(),
        )

        assertEmpty(redundantElseFixes())
    }

    private fun launchFix() {
        myFixture.launchAction(redundantElseFixes().single())
    }

    private fun redundantElseFixes() = myFixture.getAllQuickFixes().filter { it.text == "Remove redundant 'else'" }
}

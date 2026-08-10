package io.runescript.plugin.ide.inspections

import io.runescript.plugin.lang.parser.RsParserTestCase

class RuneScriptEmptyControlFlowBodyInspectionTest : RsParserTestCase() {
    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(RuneScriptEmptyControlFlowBodyInspection())
    }

    fun testHighlightsEmptyControlFlowBodies() {
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main]
            {
                if (${'$'}value = 1) <weak_warning descr="Empty 'if' body">{}</weak_warning>
                else <weak_warning descr="Empty 'else' body">;</weak_warning>
                while (${'$'}value < 10) <weak_warning descr="Empty 'while' body">{ }</weak_warning>
                switch_int (${'$'}value) {
                    <weak_warning descr="Empty switch case body">case</weak_warning> 1 :
                    case 2 : handle();
                }
            }
            """.trimIndent(),
        )

        myFixture.checkHighlighting()
    }

    fun testDoesNotHighlightCommentedOrPopulatedBodies() {
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main]
            {
                if (${'$'}value = 1) {
                    // Intentionally ignored.
                }
                while (${'$'}value < 10) tick();
                switch_int (${'$'}value) {
                    case 1 :
                        // Intentionally ignored.
                    case 2 : handle();
                }
            }
            """.trimIndent(),
        )

        myFixture.checkHighlighting()
    }

    fun testTreatsSemicolonOnlyBlocksAndCasesAsEmpty() {
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main]
            {
                if (${'$'}value = 1) <weak_warning descr="Empty 'if' body">{ ; }</weak_warning>
                while (${'$'}value < 10) <weak_warning descr="Empty 'while' body">{
                    ;
                    ;
                }</weak_warning>
                switch_int (${'$'}value) {
                    <weak_warning descr="Empty switch case body">case</weak_warning> 1 : ;
                }
            }
            """.trimIndent(),
        )

        myFixture.checkHighlighting()
    }
}

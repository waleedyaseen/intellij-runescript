package io.runescript.plugin.ide.inspections

import io.runescript.plugin.lang.parser.RsParserTestCase

class RuneScriptUnusedLocalVariableInspectionTest : RsParserTestCase() {
    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(RuneScriptUnusedLocalVariableInspection())
    }

    fun testReportsUnusedDeclaration() {
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main]
            {
                <warning descr="Unused local variable">def_int ${"$"}unused = 1;</warning>
            }
            """.trimIndent(),
        )

        myFixture.checkHighlighting()
    }

    fun testDoesNotReportReferencedDeclaration() {
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main]
            {
                def_int ${"$"}used = 1;
                ${"$"}used = 2;
            }
            """.trimIndent(),
        )

        myFixture.checkHighlighting()
    }

    fun testReferenceInAnotherScriptDoesNotMarkDeclarationUsed() {
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,first]
            {
                <warning descr="Unused local variable">def_int ${"$"}value = 1;</warning>
            }

            [proc,second]
            {
                ${"$"}value = 2;
            }
            """.trimIndent(),
        )

        myFixture.checkHighlighting()
    }
}

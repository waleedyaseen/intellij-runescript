package io.runescript.plugin.ide.inspections

import io.runescript.plugin.ide.neptune.NeptuneProjectImportData
import io.runescript.plugin.ide.neptune.neptuneModuleData
import io.runescript.plugin.lang.parser.RsParserTestCase

class RuneScriptParameterConstantInspectionTest : RsParserTestCase() {
    override fun setUp() {
        super.setUp()
        module.neptuneModuleData.updateFromImportData(
            NeptuneProjectImportData(
                name = "test",
                sourcePaths = emptyList(),
                symbolPaths = listOf("symbols"),
                dbFindReturnsCount = true,
                ccCreateAssertNewArg = true,
                prefixPostfixExpressions = true,
                arraysV2 = true,
                simplifiedTypeCodes = true,
            ),
        )
        myFixture.addFileToProject(
            "symbols/constant.sym",
            """
            align_left	0
            align_centre	1
            align_other	2
            vertical_top	0
            """.trimIndent() + "\n",
        )
        myFixture.addFileToProject(
            "commands.cs2",
            """
            /**
             * @parammeta alignment constant[align_left, align_centre]
             */
            [command,set_alignment](int ${"$"}alignment)
            """.trimIndent(),
        )
        myFixture.enableInspections(RuneScriptParameterConstantInspection())
    }

    fun testAcceptsDocumentedConstantAndReplacesMatchingLiteral() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    set_alignment(^align_left);
                    set_alignment(<warning descr="Value must be one of: ^align_left, ^align_centre">1</warning>);
                }
                """.trimIndent(),
            )

        myFixture.checkHighlighting()
        val fix = myFixture.getAllQuickFixes().single { action -> action.text == "Replace with ^align_centre" }
        myFixture.launchAction(fix)

        assertTrue(file.text.contains("set_alignment(^align_centre);"))
    }

    fun testRejectsOtherConstantsAndInvalidLiterals() {
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main]
            {
                set_alignment(<warning descr="Value must be one of: ^align_left, ^align_centre">^align_other</warning>);
                set_alignment(<warning descr="Value must be one of: ^align_left, ^align_centre">99</warning>);
            }
            """.trimIndent(),
        )

        myFixture.checkHighlighting()
        assertContainsElements(
            myFixture.getAllQuickFixes().map { action -> action.text },
            "Replace with ^align_left",
            "Replace with ^align_centre",
        )
    }

    fun testPrefersAllowedConstantWithSameResolvedValue() {
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main]
            {
                set_alignment(<warning descr="Value must be one of: ^align_left, ^align_centre">^vertical_top</warning>);
            }
            """.trimIndent(),
        )

        myFixture.checkHighlighting()
        assertEquals(
            listOf("Replace with ^align_left"),
            myFixture
                .getAllQuickFixes()
                .map { action -> action.text }
                .filter { text -> text.startsWith("Replace with ^") },
        )
    }
}

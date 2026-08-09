package io.runescript.plugin.ide.inspections

import io.runescript.plugin.lang.parser.RsParserTestCase

class RuneScriptTypeCheckerInspectionTest : RsParserTestCase() {
    fun testUnresolvedCallsOfferScriptCreationFixes() {
        myFixture.addFileToProject("neptune.toml", "")
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    missing_command();
                    ~missing_proc();
                }
                """.trimIndent(),
            )
        myFixture.enableInspections(RuneScriptTypeCheckerInspection())

        val fixes = myFixture.getAllQuickFixes().associateBy { it.text }

        assertContainsElements(
            fixes.keys,
            "Create script ('missing_command')",
            "Create script ('missing_proc')",
        )
        myFixture.launchAction(fixes.getValue("Create script ('missing_proc')"))
        assertTrue(file.text.contains("[proc,missing_proc]"))
    }
}

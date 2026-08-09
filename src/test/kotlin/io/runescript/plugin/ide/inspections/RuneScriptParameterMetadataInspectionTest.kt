package io.runescript.plugin.ide.inspections

import io.runescript.plugin.ide.neptune.NeptuneProjectImportData
import io.runescript.plugin.ide.neptune.neptuneModuleData
import io.runescript.plugin.lang.parser.RsParserTestCase

class RuneScriptParameterMetadataInspectionTest : RsParserTestCase() {
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
        myFixture.addFileToProject("symbols/constant.sym", "align_left\t0\n")
        myFixture.enableInspections(RuneScriptParameterMetadataInspection())
    }

    fun testValidatesBehaviorNamesTypesOptionsAndConstants() {
        myFixture.configureByText(
            "commands.cs2",
            """
            /**
             * <warning descr="Unknown parameter behavior 'colour'">@parammeta value colour</warning>
             * <warning descr="'rgb' behavior requires an int parameter">@parammeta text rgb</warning>
             * <warning descr="'argb' behavior does not accept options">@parammeta value argb[extra]</warning>
             * <warning descr="Unknown constant '^missing'">@parammeta value constant[align_left, missing]</warning>
             * <warning descr="Duplicate 'constant' behavior for 'value'">@parammeta value constant[align_left]</warning>
             */
            [command,target](int ${"$"}value, string ${"$"}text)
            """.trimIndent(),
        )

        myFixture.checkHighlighting()
    }

    fun testRenamesUnknownMetadataParameter() {
        val file =
            myFixture.configureByText(
                "commands.cs2",
                """
                /**
                 * @parammeta <warning descr="Unknown parameter 'colour'">colour</warning> rgb
                 */
                [command,target](int ${"$"}color)
                """.trimIndent(),
            )

        myFixture.checkHighlighting()
        val fix = myFixture.getAllQuickFixes().single { action -> action.text == "Change metadata parameter to 'color'" }
        myFixture.launchAction(fix)

        assertTrue(file.text.contains("@parammeta color rgb"))
    }
}

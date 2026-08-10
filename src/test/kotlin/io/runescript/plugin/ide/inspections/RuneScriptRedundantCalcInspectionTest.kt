package io.runescript.plugin.ide.inspections

import com.intellij.openapi.editor.colors.CodeInsightColors
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class RuneScriptRedundantCalcInspectionTest : BasePlatformTestCase() {
    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(RuneScriptRedundantCalcInspection())
    }

    fun testReportsCalcAroundCompleteExpressions() {
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main](int ${'$'}value, intarray ${'$'}values)
            {
                def_int ${'$'}literal = <weak_warning descr="Redundant 'calc' wrapper">calc</weak_warning>(145);
                def_int ${'$'}local = <weak_warning descr="Redundant 'calc' wrapper">calc</weak_warning>(${'$'}value);
                def_int ${'$'}constant = <weak_warning descr="Redundant 'calc' wrapper">calc</weak_warning>(^width);
                def_int ${'$'}scoped = <weak_warning descr="Redundant 'calc' wrapper">calc</weak_warning>(%counter);
                def_int ${'$'}array = <weak_warning descr="Redundant 'calc' wrapper">calc</weak_warning>(${'$'}values(${'$'}value));
                def_int ${'$'}command = <weak_warning descr="Redundant 'calc' wrapper">calc</weak_warning>(clientclock());
                def_int ${'$'}proc = <weak_warning descr="Redundant 'calc' wrapper">calc</weak_warning>(~value());
            }
            """.trimIndent(),
        )

        myFixture.checkHighlighting()
        val redundantCalcHighlights =
            myFixture.doHighlighting().filter { it.description == "Redundant 'calc' wrapper" }
        assertSize(7, redundantCalcHighlights)
        assertTrue(
            redundantCalcHighlights.all {
                it.type.attributesKey == CodeInsightColors.NOT_USED_ELEMENT_ATTRIBUTES
            },
        )
    }

    fun testKeepsCalcContainingArithmetic() {
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main](int ${'$'}value)
            {
                def_int ${'$'}sum = calc(${'$'}value + 1);
                def_int ${'$'}grouped = calc((${'$'}value + 1) * 2);
                def_int ${'$'}bits = calc(${'$'}value & 0xff);
            }
            """.trimIndent(),
        )

        myFixture.checkHighlighting()
        assertEmpty(myFixture.getAllQuickFixes().filter { it.text == "Remove redundant 'calc'" })
    }

    fun testOnlyReportsRedundantOuterNestedCalc() {
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main](int ${'$'}value)
            def_int ${'$'}result = <weak_warning descr="Redundant 'calc' wrapper">calc</weak_warning>(calc(${'$'}value + 1));
            """.trimIndent(),
        )

        myFixture.checkHighlighting()
    }

    fun testRemovesWrapperAndPreservesInnerComment() {
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main](int ${'$'}value)
            def_int ${'$'}result = calc(/* keep */ ${'$'}value);
            """.trimIndent(),
        )

        val fix = myFixture.getAllQuickFixes().single { it.text == "Remove redundant 'calc'" }
        myFixture.launchAction(fix)

        myFixture.checkResult(
            """
            [proc,main](int ${'$'}value)
            def_int ${'$'}result = /* keep */ ${'$'}value;
            """.trimIndent(),
        )
    }

    fun testRemovesCalcInsideStringInterpolation() {
        myFixture.configureByText("main.cs2", "[proc,main](int ${'$'}value)\ndebug(\"<calc(${'$'}value)>\");")

        val fix = myFixture.getAllQuickFixes().single { it.text == "Remove redundant 'calc'" }
        myFixture.launchAction(fix)

        myFixture.checkResult("[proc,main](int ${'$'}value)\ndebug(\"<${'$'}value>\");")
    }
}

package io.runescript.plugin.ide.actions

import io.runescript.plugin.lang.parser.RsParserTestCase

class RsGenerateActionsTest : RsParserTestCase() {
    fun testOffersScriptActionsOnlyOnSignature() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,<caret>main]
                return;
                """.trimIndent(),
            )

        assertTrue(RsGenerateDocAction().isAvailable(myFixture.editor, file))
        assertTrue(RsGenerateScriptWrapperAction().isAvailable(myFixture.editor, file))

        myFixture.editor.caretModel.moveToOffset(file.text.indexOf("return"))
        assertFalse(RsGenerateDocAction().isAvailable(myFixture.editor, file))
        assertFalse(RsGenerateScriptWrapperAction().isAvailable(myFixture.editor, file))
    }

    fun testOffersSwitchActionsOnSwitchKeyword() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main](int ${'$'}value)
                <caret>switch_int (${'$'}value) {
                }
                """.trimIndent(),
            )

        assertTrue(RsGenerateSwitchCaseAction().isAvailable(myFixture.editor, file))
        assertTrue(RsGenerateSwitchDefaultAction().isAvailable(myFixture.editor, file))
    }
}

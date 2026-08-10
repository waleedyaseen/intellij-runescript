package io.runescript.plugin.ide.codeInsight.intention.controlFlow

import com.intellij.openapi.command.WriteCommandAction
import io.runescript.plugin.lang.parser.RsParserTestCase

class RsSplitSwitchCaseIntentionTest : RsParserTestCase() {
    fun testSplitsValuesAndDuplicatesBody() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    switch_int (${'$'}value) {
                        <caret>case 1, 2, 3 :
                            log();
                            handle();
                        case default :
                            fallback();
                    }
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!
        val intention = RsSplitSwitchCaseIntention()
        assertTrue(intention.isAvailable(project, myFixture.editor, element))

        WriteCommandAction.runWriteCommandAction(project) {
            intention.invoke(project, myFixture.editor, element)
        }

        myFixture.checkResult(
            """
            [proc,main]
            {
                switch_int (${'$'}value) {
                    <caret>case 1 :
                        log();
                        handle();
                    case 2 :
                        log();
                        handle();
                    case 3 :
                        log();
                        handle();
                    case default :
                        fallback();
                }
            }
            """.trimIndent(),
        )
    }

    fun testUnavailableForSingleValueCase() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    switch_int (${'$'}value) {
                        <caret>case 1 :
                            one();
                    }
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!

        assertFalse(RsSplitSwitchCaseIntention().isAvailable(project, myFixture.editor, element))
    }
}

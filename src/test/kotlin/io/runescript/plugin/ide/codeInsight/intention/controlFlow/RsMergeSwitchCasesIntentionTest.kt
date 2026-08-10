package io.runescript.plugin.ide.codeInsight.intention.controlFlow

import com.intellij.openapi.command.WriteCommandAction
import io.runescript.plugin.lang.parser.RsParserTestCase

class RsMergeSwitchCasesIntentionTest : RsParserTestCase() {
    fun testMergesAdjacentCasesWithIdenticalBodies() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    switch_int (${'$'}value) {
                        <caret>case 1, 2 :
                            log();
                            handle();
                        case 3 :
                            log();
                            handle();
                        case 4 :
                            other();
                    }
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!
        val intention = RsMergeSwitchCasesIntention()
        assertTrue(intention.isAvailable(project, myFixture.editor, element))

        WriteCommandAction.runWriteCommandAction(project) {
            intention.invoke(project, myFixture.editor, element)
        }

        myFixture.checkResult(
            """
            [proc,main]
            {
                switch_int (${'$'}value) {
                    <caret>case 1, 2, 3 :
                        log();
                        handle();
                    case 4 :
                        other();
                }
            }
            """.trimIndent(),
        )
    }

    fun testUnavailableForDifferentBodies() {
        assertUnavailable(
            """
            [proc,main]
            {
                switch_int (${'$'}value) {
                    <caret>case 1 :
                        one();
                    case 2 :
                        two();
                }
            }
            """.trimIndent(),
        )
    }

    fun testUnavailableWhenNextCaseIsDefault() {
        assertUnavailable(
            """
            [proc,main]
            {
                switch_int (${'$'}value) {
                    <caret>case 1 :
                        fallback();
                    case default :
                        fallback();
                }
            }
            """.trimIndent(),
        )
    }

    fun testPreservesCommentBetweenMergedCases() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    switch_int (${'$'}value) {
                        <caret>case 1 : handle();
                        // The second value uses the same behavior.
                        case 2 : handle();
                    }
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!

        WriteCommandAction.runWriteCommandAction(project) {
            RsMergeSwitchCasesIntention().invoke(project, myFixture.editor, element)
        }

        myFixture.checkResult(
            """
            [proc,main]
            {
                switch_int (${'$'}value) {
                    <caret>case 1, 2 :
                        // The second value uses the same behavior.
                        handle();
                }
            }
            """.trimIndent(),
        )
    }

    private fun assertUnavailable(source: String) {
        val file = myFixture.configureByText("main.cs2", source)
        val element = file.findElementAt(myFixture.caretOffset)!!

        assertFalse(RsMergeSwitchCasesIntention().isAvailable(project, myFixture.editor, element))
    }
}

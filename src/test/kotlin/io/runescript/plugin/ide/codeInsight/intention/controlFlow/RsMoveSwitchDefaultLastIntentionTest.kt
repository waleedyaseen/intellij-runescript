package io.runescript.plugin.ide.codeInsight.intention.controlFlow

import com.intellij.openapi.command.WriteCommandAction
import io.runescript.plugin.lang.parser.RsParserTestCase

class RsMoveSwitchDefaultLastIntentionTest : RsParserTestCase() {
    fun testMovesDefaultAndItsBodyAfterOtherCases() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    switch_int (${'$'}value) {
                        case <caret>default :
                            fallback();
                        case 1 :
                            one();
                        case 2 :
                            two();
                    }
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!
        val intention = RsMoveSwitchDefaultLastIntention()
        assertTrue(intention.isAvailable(project, myFixture.editor, element))

        WriteCommandAction.runWriteCommandAction(project) {
            intention.invoke(project, myFixture.editor, element)
        }

        myFixture.checkResult(
            """
            [proc,main]
            {
                switch_int (${'$'}value) {
                    case 1 :
                        one();
                    case 2 :
                        two();
                    case <caret>default :
                        fallback();
                }
            }
            """.trimIndent(),
        )
    }

    fun testUnavailableWhenDefaultIsAlreadyLast() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    switch_int (${'$'}value) {
                        case 1 :
                            one();
                        case <caret>default :
                            fallback();
                    }
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!

        assertFalse(RsMoveSwitchDefaultLastIntention().isAvailable(project, myFixture.editor, element))
    }

    fun testMovesLeadingCommentWithDefaultCase() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    switch_int (${'$'}value) {
                        // Fallback behavior.
                        case <caret>default : fallback();
                        // Exact behavior.
                        case 1 : one();
                    }
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!

        WriteCommandAction.runWriteCommandAction(project) {
            RsMoveSwitchDefaultLastIntention().invoke(project, myFixture.editor, element)
        }

        myFixture.checkResult(
            """
            [proc,main]
            {
                switch_int (${'$'}value) {
                    // Exact behavior.
                    case 1 :
                        one();
                    // Fallback behavior.
                    case <caret>default :
                        fallback();
                }
            }
            """.trimIndent(),
        )
    }
}

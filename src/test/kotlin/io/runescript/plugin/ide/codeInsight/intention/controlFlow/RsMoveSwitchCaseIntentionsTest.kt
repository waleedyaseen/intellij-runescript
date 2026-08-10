package io.runescript.plugin.ide.codeInsight.intention.controlFlow

import com.intellij.openapi.command.WriteCommandAction
import io.runescript.plugin.lang.parser.RsParserTestCase

class RsMoveSwitchCaseIntentionsTest : RsParserTestCase() {
    fun testMovesCaseUp() {
        applyIntention(
            RsMoveSwitchCaseUpIntention(),
            """
            [proc,main]
            {
                switch_int (${'$'}value) {
                    case 1 : one();
                    <caret>case 2 : two();
                    case default : fallback();
                }
            }
            """.trimIndent(),
            """
            [proc,main]
            {
                switch_int (${'$'}value) {
                    case 2 :
                        two();
                    case 1 :
                        one();
                    case default :
                        fallback();
                }
            }
            """.trimIndent(),
        )
    }

    fun testMovesCaseDown() {
        applyIntention(
            RsMoveSwitchCaseDownIntention(),
            """
            [proc,main]
            {
                switch_int (${'$'}value) {
                    <caret>case 1 : one();
                    case 2 : two();
                    case default : fallback();
                }
            }
            """.trimIndent(),
            """
            [proc,main]
            {
                switch_int (${'$'}value) {
                    case 2 :
                        two();
                    case 1 :
                        one();
                    case default :
                        fallback();
                }
            }
            """.trimIndent(),
        )
    }

    fun testCannotMoveOrdinaryCaseBelowDefault() {
        assertUnavailable(
            RsMoveSwitchCaseDownIntention(),
            """
            [proc,main]
            {
                switch_int (${'$'}value) {
                    <caret>case 1 : one();
                    case default : fallback();
                }
            }
            """.trimIndent(),
        )
    }

    fun testCannotMoveDefaultUp() {
        assertUnavailable(
            RsMoveSwitchCaseUpIntention(),
            """
            [proc,main]
            {
                switch_int (${'$'}value) {
                    case 1 : one();
                    <caret>case default : fallback();
                }
            }
            """.trimIndent(),
        )
    }

    fun testUnavailableUntilMisplacedDefaultIsFixed() {
        assertUnavailable(
            RsMoveSwitchCaseUpIntention(),
            """
            [proc,main]
            {
                switch_int (${'$'}value) {
                    case default : fallback();
                    <caret>case 1 : one();
                    case 2 : two();
                }
            }
            """.trimIndent(),
        )
    }

    fun testMovesLeadingCommentWithCase() {
        applyIntention(
            RsMoveSwitchCaseUpIntention(),
            """
            [proc,main]
            {
                switch_int (${'$'}value) {
                    // First case.
                    case 1 : one();
                    // Second case.
                    <caret>case 2 : two();
                }
            }
            """.trimIndent(),
            """
            [proc,main]
            {
                switch_int (${'$'}value) {
                    // Second case.
                    case 2 :
                        two();
                    // First case.
                    case 1 :
                        one();
                }
            }
            """.trimIndent(),
        )
    }

    private fun applyIntention(
        intention: RsMoveSwitchCaseIntention,
        source: String,
        expected: String,
    ) {
        val file = myFixture.configureByText("main.cs2", source)
        val element = file.findElementAt(myFixture.caretOffset)!!
        assertTrue(intention.isAvailable(project, myFixture.editor, element))

        WriteCommandAction.runWriteCommandAction(project) {
            intention.invoke(project, myFixture.editor, element)
        }

        myFixture.checkResult(expected)
    }

    private fun assertUnavailable(
        intention: RsMoveSwitchCaseIntention,
        source: String,
    ) {
        val file = myFixture.configureByText("main.cs2", source)
        val element = file.findElementAt(myFixture.caretOffset)!!

        assertFalse(intention.isAvailable(project, myFixture.editor, element))
    }
}

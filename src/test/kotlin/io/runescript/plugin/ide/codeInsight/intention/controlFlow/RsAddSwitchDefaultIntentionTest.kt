package io.runescript.plugin.ide.codeInsight.intention.controlFlow

import com.intellij.openapi.command.WriteCommandAction
import io.runescript.plugin.lang.parser.RsParserTestCase

class RsAddSwitchDefaultIntentionTest : RsParserTestCase() {
    fun testAddsDefaultAfterExistingCases() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    <caret>switch_int (${'$'}value) {
                        case 1 :
                            foo();
                    }
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!
        val intention = RsAddSwitchDefaultIntention()
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
                        foo();
                    case default :
                        <caret>
                }
            }
            """.trimIndent(),
        )
    }

    fun testAddsDefaultToEmptySwitch() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    <caret>switch_int (${'$'}value) {
                    }
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!
        val intention = RsAddSwitchDefaultIntention()

        WriteCommandAction.runWriteCommandAction(project) {
            intention.invoke(project, myFixture.editor, element)
        }

        myFixture.checkResult(
            """
            [proc,main]
            {
                switch_int (${'$'}value) {
                    case default :
                        <caret>
                }
            }
            """.trimIndent(),
        )
    }

    fun testUnavailableWhenDefaultAlreadyExists() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    <caret>switch_int (${'$'}value) {
                        case default :
                            foo();
                    }
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!

        assertFalse(RsAddSwitchDefaultIntention().isAvailable(project, myFixture.editor, element))
    }

    fun testDefaultTextInCommentDoesNotMoveCaret() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    <caret>switch_int (${'$'}value) {
                        // case default : is documentation, not a label.
                        case 1 : one();
                    }
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!

        WriteCommandAction.runWriteCommandAction(project) {
            RsAddSwitchDefaultIntention().invoke(project, myFixture.editor, element)
        }

        myFixture.checkResult(
            """
            [proc,main]
            {
                switch_int (${'$'}value) {
                    // case default : is documentation, not a label.
                    case 1 :
                        one();
                    case default :
                        <caret>
                }
            }
            """.trimIndent(),
        )
    }
}

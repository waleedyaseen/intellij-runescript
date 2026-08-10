package io.runescript.plugin.ide.codeInsight.intention.controlFlow

import com.intellij.openapi.command.WriteCommandAction
import io.runescript.plugin.lang.parser.RsParserTestCase

class RsAddElseBranchIntentionTest : RsParserTestCase() {
    fun testAddsElseBranchAndPlacesCaretInside() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    <caret>if (${'$'}ready = true) {
                        start();
                    }
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!
        val intention = RsAddElseBranchIntention()
        assertTrue(intention.isAvailable(project, myFixture.editor, element))

        WriteCommandAction.runWriteCommandAction(project) {
            intention.invoke(project, myFixture.editor, element)
        }

        myFixture.checkResult(
            """
            [proc,main]
            {
                if (${'$'}ready = true) {
                    start();
                } else {
                    <caret>
                }
            }
            """.trimIndent(),
        )
    }

    fun testAddsElseBranchToSingleStatementIf() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    <caret>if (${'$'}ready = true) start();
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!
        val intention = RsAddElseBranchIntention()

        WriteCommandAction.runWriteCommandAction(project) {
            intention.invoke(project, myFixture.editor, element)
        }

        myFixture.checkResult(
            """
            [proc,main]
            {
                if (${'$'}ready = true) start(); else {
                    <caret>
                }
            }
            """.trimIndent(),
        )
    }

    fun testUnavailableWhenElseAlreadyExists() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    <caret>if (${'$'}ready = true) start();
                    else stop();
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!

        assertFalse(RsAddElseBranchIntention().isAvailable(project, myFixture.editor, element))
    }

    fun testNestedElseDoesNotConfusePlaceholderRemoval() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    <caret>if (${'$'}outer = true) {
                        if (${'$'}inner = true) one(); else two();
                    }
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!

        WriteCommandAction.runWriteCommandAction(project) {
            RsAddElseBranchIntention().invoke(project, myFixture.editor, element)
        }

        myFixture.checkResult(
            """
            [proc,main]
            {
                if (${'$'}outer = true) {
                    if (${'$'}inner = true) one(); else two();
                } else {
                    <caret>
                }
            }
            """.trimIndent(),
        )
    }
}

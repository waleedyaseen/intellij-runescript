package io.runescript.plugin.ide.codeInsight.intention.controlFlow

import com.intellij.openapi.command.WriteCommandAction
import io.runescript.plugin.lang.parser.RsParserTestCase

class RsMergeEquivalentElseIfIntentionTest : RsParserTestCase() {
    fun testMergesMatchingBranchesAndPreservesTail() {
        applyIntention(
            """
            [proc,main]
            {
                <caret>if (${'$'}value = 1) {
                    handle();
                } else if (${'$'}value = 2) {
                    handle();
                } else {
                    fallback();
                }
            }
            """.trimIndent(),
            """
            [proc,main]
            {
                if ((${'$'}value = 1) | (${'$'}value = 2)) {
                    handle();
                } else {
                    fallback();
                }
            }
            """.trimIndent(),
        )
    }

    fun testMergesSingleStatementBranchesWithoutTail() {
        applyIntention(
            """
            [proc,main]
            {
                <caret>if (${'$'}value = 1) handle();
                else if (${'$'}value = 2) handle();
            }
            """.trimIndent(),
            """
            [proc,main]
            {
                if ((${'$'}value = 1) | (${'$'}value = 2)) {
                    handle();
                }
            }
            """.trimIndent(),
        )
    }

    fun testUnavailableForDifferentBodies() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    <caret>if (${'$'}value = 1) one();
                    else if (${'$'}value = 2) two();
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!

        assertFalse(RsMergeEquivalentElseIfIntention().isAvailable(project, myFixture.editor, element))
    }

    fun testUnavailableWhenInterBranchCommentWouldBeLost() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    <caret>if (${'$'}value = 1) handle();
                    // Keep this branch explanation.
                    else if (${'$'}value = 2) handle();
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!

        assertFalse(RsMergeEquivalentElseIfIntention().isAvailable(project, myFixture.editor, element))
    }

    private fun applyIntention(
        source: String,
        expected: String,
    ) {
        val file = myFixture.configureByText("main.cs2", source)
        val element = file.findElementAt(myFixture.caretOffset)!!
        val intention = RsMergeEquivalentElseIfIntention()
        assertTrue(intention.isAvailable(project, myFixture.editor, element))

        WriteCommandAction.runWriteCommandAction(project) {
            intention.invoke(project, myFixture.editor, element)
        }

        myFixture.checkResult(expected)
    }
}
